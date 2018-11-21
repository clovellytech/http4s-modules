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
import tsec.passwordhashers.jca.{BCrypt, JCAPasswordPlatform}
import tsec.authentication._
import db.domain.User
import domain.Error
import domain.users.UserRepositoryAlgebra
import domain.tokens.TokenRepositoryAlgebra
import doobie.util.transactor.Transactor
import infrastructure.authentication.TransBackingStore._
import infrastructure.repository.persistent.{TokenRepositoryInterpreter, UserRepositoryInterpreter}


object AuthEndpoints {
  def persistingEndpoints[F[_] : Sync, A](xa: Transactor[F], crypt: JCAPasswordPlatform[A] = BCrypt)(
    implicit P : PasswordHasher[F, A]
  ) : AuthEndpoints[F, A] = {
    implicit val userService = new UserRepositoryInterpreter(xa)
    implicit val tokenService = new TokenRepositoryInterpreter(xa)
    val authEndpoints =  new AuthEndpoints(crypt)
    authEndpoints
  }
}

class AuthEndpoints[F[_] : Sync : UserRepositoryAlgebra : TokenRepositoryAlgebra, A](hasher : JCAPasswordPlatform[A])(
  implicit P : PasswordHasher[F, A]
)
extends Http4sDsl[F] {
  val userService = implicitly[UserRepositoryAlgebra[F]]
  val tokenService = implicitly[TokenRepositoryAlgebra[F]]
  val F = implicitly[Sync[F]]

  val bearerTokenAuth = BearerTokenAuthenticator(
    tokenTrans(tokenService),
    userTrans(userService),
    TSecTokenSettings(
      expiryDuration = 10.minutes,
      maxIdle = None
    )
  )

  val Auth = SecuredRequestHandler(bearerTokenAuth)

  val unauthService : HttpRoutes[F] = HttpRoutes.of {
    case req @ POST -> Root / "user" => for {
      userRequest <- req.as[UserRequest]
      foundUser <- userService.byUsername(userRequest.username).isDefined
      _ <- if(foundUser) F.raiseError(Error.Duplicate()) else ().pure[F]
      hash <- hasher.hashpw[F](userRequest.password)
      user = User(userRequest.username, hash.getBytes)
      _ <- userService.insertGetId(user).getOrElseF(F.raiseError(Error.Duplicate()))
      result <- Ok()
    } yield result

    case req @ POST -> Root / "login" => for {
      userRequest <- req.as[UserRequest]
      u <- userService.byUsername(userRequest.username).toRight(Error.NotFound() : Throwable).value.flatMap(_.raiseOrPure[F])
      (user, uuid, joinTime) = u
      hash = PasswordHash[A](new String(user.hash))
      status <- hasher.checkpw[F](userRequest.password, hash)
      resp <- if(status == Verified) Ok() else F.raiseError(Error.BadLogin() : Throwable)
      tok <- bearerTokenAuth.create(uuid)
    } yield bearerTokenAuth.embed(resp, tok)
  }


  val authService: BearerAuthService[F] = {
    def respUser(ou : OptionT[F, (User, UUID, Instant)]) : F[Response[F]] = for {
      ou2 <- ou.value
      u <- F.fromOption(ou2, Error.NotFound())
      (user, _, joinTime) = u
      resp <- Ok(UserDetail(user.username, joinTime).asJson)
    } yield resp

    BearerAuthService {
      case req@GET -> Root / "user" asAuthed _ => respUser(userService.byId(req.authenticator.identity))

      case GET -> Root / "user" / name asAuthed _ => respUser(userService.byUsername(name))
    }
  }

  def endpoints : HttpRoutes[F] = unauthService <+> Auth.liftService(authService)
}


