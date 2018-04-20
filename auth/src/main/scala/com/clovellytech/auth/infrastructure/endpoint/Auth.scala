package com.clovellytech.auth
package infrastructure.endpoint

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
import domain.users.UserService
import domain.tokens.TokenService
import infrastructure.authentication.TransBackingStore._

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
    case req @ POST -> Root / "user" => for {
      userRequest <- req.as[UserRequest]
      hash <- hasher.hashpw[F](userRequest.password)
      _ <- userService.insert(User(userRequest.username, hash.getBytes))
      result <- Ok()
    } yield result

    case req @ POST -> Root / "login" => {
      val r = for {
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


  val authService: BearerAuthService[F] = BearerAuthService {
    case GET -> Root / "user" / name asAuthed _ =>
      (for {
        u <- userService.byUsername(name)
        (user, _, joinTime) = u
        resp <- OptionT.liftF(Ok(UserDetail(user.username, joinTime).asJson))
      } yield resp).getOrElseF(BadRequest())
  }

  def endpoints : HttpService[F] = unauthService <+> Auth.liftService(authService)
}
