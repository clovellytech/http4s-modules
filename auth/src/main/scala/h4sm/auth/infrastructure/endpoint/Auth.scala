package h4sm.auth
package infrastructure.endpoint

import java.util.UUID

import scala.concurrent.duration._
import cats.data.OptionT
import cats.effect.Sync
import cats.implicits._
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import tsec.common._
import tsec.passwordhashers.{PasswordHash, PasswordHasher}
import tsec.passwordhashers.jca.JCAPasswordPlatform
import tsec.authentication._
import db.domain.User
import domain.Error
import domain.users.UserRepositoryAlgebra
import domain.tokens.{AsBaseToken, TokenRepositoryAlgebra}
import infrastructure.authentication.TransBackingStore._
import domain.tokens.AsBaseToken.ops._
import domain.tokens._
import tsec.cipher.symmetric.{AES, IvGen}
import tsec.cipher.symmetric.jca._

object Authenticators {

  def statelessCookie[F[_]: Sync: UserRepositoryAlgebra, Alg: JAuthEncryptor[F, ?]: IvGen[F, ?]](
    key: SecretKey[Alg],
    expiryDuration: FiniteDuration = 10.minutes, 
    maxIdle: Option[FiniteDuration] = None
  )(implicit a: AES[Alg]): UserAuthenticator[F, AuthEncryptedCookie[Alg, ?]] = 
    EncryptedCookieAuthenticator.stateless(
      TSecCookieSettings(
        cookieName = "ct-auth",
        secure = true,
        expiryDuration = expiryDuration,
        maxIdle = maxIdle
      ),
      userTrans(UserRepositoryAlgebra[F]),
      key
    )

  def bearer[F[_]: Sync: UserRepositoryAlgebra: TokenRepositoryAlgebra]: UserAuthenticator[F, TSecBearerToken] = BearerTokenAuthenticator(
    tokenTrans[TSecBearerToken].apply(TokenRepositoryAlgebra[F]),
    userTrans(UserRepositoryAlgebra[F]),
    TSecTokenSettings(
      expiryDuration = 10.minutes,
      maxIdle = None
    )
  )
}


class AuthEndpoints[F[_] : Sync : UserRepositoryAlgebra, A, T[_]](
  hasher : JCAPasswordPlatform[A],
  authenticator: UserAuthenticator[F, T]
)(
  implicit P : PasswordHasher[F, A],
  A: AsBaseToken[T[UserId]]
)
extends Http4sDsl[F] {
  type Token = T[UserId]

  implicit val boolEncoder : EntityEncoder[F, Boolean] = jsonEncoderOf

  val userService = implicitly[UserRepositoryAlgebra[F]]

  val Auth = SecuredRequestHandler(authenticator)

  val unauthService : HttpRoutes[F] = HttpRoutes.of {
    case req @ POST -> Root / "user" => {
      val res: F[Response[F]] = for {
        userRequest <- req.as[UserRequest]
        foundUser <- UserRepositoryAlgebra[F].byUsername(userRequest.username).isDefined
        _ <- if(foundUser) Sync[F].raiseError(Error.Duplicate()) else ().pure[F]
        hash <- hasher.hashpw[F](userRequest.password.getBytes())
        user = User(userRequest.username, hash.getBytes)
        _ <- UserRepositoryAlgebra[F].insertGetId(user).getOrElseF(Sync[F].raiseError(Error.Duplicate()))
        result <- Ok()
      } yield result

      res.recoverWith{ case _ => BadRequest() }
    }

    case req @ POST -> Root / "login" => {
      val res : F[Response[F]] = for {
        userRequest <- req.as[UserRequest]
        u <- UserRepositoryAlgebra[F].byUsername(userRequest.username).toRight(Error.NotFound() : Throwable).value.flatMap(_.raiseOrPure[F])
        (user, uuid, joinTime) = u
        hash = PasswordHash[A](new String(user.hash))
        status <- hasher.checkpw[F](userRequest.password.getBytes, hash)
        resp <- if(status == Verified) Ok() else Sync[F].raiseError(Error.BadLogin() : Throwable)
        tok <- authenticator.create(uuid)
      } yield authenticator.embed(resp, tok)

      res.recoverWith{ case _ => BadRequest() }
    }

    case GET -> Root / "exists" / username =>
      UserRepositoryAlgebra[F].byUsername(username).isDefined.flatMap(Ok apply _)
  }

  val authService: UserAuthService[F, T] = {
    def respUser(ou : OptionT[F, (User, UUID, Instant)]) : F[Response[F]] = for {
      ou2 <- ou.value
      u <- Sync[F].fromOption(ou2, Error.NotFound())
      (user, _, joinTime) = u
      resp <- Ok(UserDetail(user.username, joinTime).asJson)
    } yield resp

    UserAuthService {
      case req@GET -> Root / "user" asAuthed _ => respUser(UserRepositoryAlgebra[F].byId(req.authenticator.asBase.identity))

      case GET -> Root / "user" / name asAuthed _ => respUser(UserRepositoryAlgebra[F].byUsername(name))
    }
  }

  def testService : HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "istest" => Ok("true")
    case DELETE -> Root / username => (for {
      u <- UserRepositoryAlgebra[F].byUsername(username)
      _ <- OptionT.liftF(UserRepositoryAlgebra[F].delete(u._2))
      resp <- OptionT.liftF(Ok())
    } yield resp).getOrElseF(BadRequest())
  }

  def endpoints : HttpRoutes[F] = unauthService <+> Auth.liftService(authService)
}


