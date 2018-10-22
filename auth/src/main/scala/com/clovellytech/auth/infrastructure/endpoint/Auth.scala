package com.clovellytech.auth
package infrastructure.endpoint

import java.util.UUID

import scala.concurrent.duration._
import cats.data.{EitherT, OptionT}
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
import domain.users.UserService
import domain.tokens.TokenService
import doobie.util.transactor.Transactor
import infrastructure.authentication.TransBackingStore._
import infrastructure.repository.persistent.{TokenRepositoryInterpreter, UserRepositoryInterpreter}

class AuthEndpoints[F[_]: Sync, A](
  userService : UserService[F],
  tokenService : TokenService[F],
  hasher : JCAPasswordPlatform[A]
)(
  implicit P : PasswordHasher[F, A]
)
extends Http4sDsl[F] {

  val bearerTokenAuth = BearerTokenAuthenticator(
    tokenTrans(tokenService.algebra),
    userTrans(userService.algebra),
    TSecTokenSettings(
      expiryDuration = 10.minutes,
      maxIdle = None
    )
  )

  val Auth = SecuredRequestHandler(bearerTokenAuth)



  val unauthService : HttpService[F] = HttpService {
    case req @ POST -> Root / "user" => {
      val job: EitherT[F, Error, Response[F]] = for {
        userRequest <- EitherT.liftF(req.as[UserRequest])
        user <- EitherT.fromOptionF(userService.byUsername(userRequest.username).value, ()).as(Error.Duplicate()).swap
        hash <- EitherT.liftF(hasher.hashpw[F](userRequest.password))
        _ <- EitherT.liftF(userService.insert(User(userRequest.username, hash.getBytes)))
        result <- EitherT.liftF(Ok())
      } yield result

      job.getOrElseF(BadRequest())
    }

    case req @ POST -> Root / "login" => {
      val r: OptionT[F, Response[F]] = for {
        userRequest <- OptionT.liftF(req.as[UserRequest])
        u <- userService.byUsername(userRequest.username)
        (user, uuid, joinTime) = u
        hash = new String(user.hash).asInstanceOf[PasswordHash[A]]
        _ <- OptionT.liftF(hasher.checkpw[F](userRequest.password, hash)).filter(_ == Verified)
        resp <- OptionT.liftF(Ok())
        tok <- OptionT.liftF(bearerTokenAuth.create(uuid))
        embedded <- OptionT.liftF(bearerTokenAuth.embed(resp, tok).pure[F])
      } yield embedded

      r.getOrElseF(BadRequest())
    }
  }


  val authService: BearerAuthService[F] = {
    def respUser(ou : OptionT[F, (User, UUID, Instant)]) : F[Response[F]] = (for {
      u <- ou
      (user, _, joinTime) = u
      resp <- OptionT.liftF(Ok(UserDetail(user.username, joinTime).asJson))
    } yield resp).getOrElseF(BadRequest())

    BearerAuthService {
      case req@GET -> Root / "user" asAuthed _ => respUser(userService.byId(req.authenticator.identity))

      case GET -> Root / "user" / name asAuthed _ => respUser(userService.byUsername(name))
    }
  }

  def endpoints : HttpService[F] = unauthService <+> Auth.liftService(authService)
}


object AuthEndpoints {

  def persistingEndpoints[F[_] : Sync, A](xa: Transactor[F], crypt: JCAPasswordPlatform[A])(
    implicit P : PasswordHasher[F, A]
  ) : AuthEndpoints[F, A] = {
    val userRepo      =  new UserRepositoryInterpreter(xa)
    val tokenRepo     =  new TokenRepositoryInterpreter(xa)
    val userService   =  new UserService(userRepo)
    val tokenService  =  new TokenService(tokenRepo)
    val authEndpoints =  new AuthEndpoints(userService, tokenService, crypt)
    authEndpoints
  }
}
