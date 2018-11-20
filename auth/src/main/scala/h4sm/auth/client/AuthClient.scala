package h4sm.auth
package client

import cats.data.OptionT
import cats.implicits._
import cats.effect.Sync
import h4sm.auth.infrastructure.endpoint.{AuthEndpoints, UserDetail, UserRequest}
import h4sm.auth.infrastructure.repository.persistent.{TokenRepositoryInterpreter, UserRepositoryInterpreter}
import org.http4s._
import org.http4s.dsl._
import org.http4s.client.dsl._
import tsec.passwordhashers.jca.BCrypt
import domain.tokens.TokenRepositoryAlgebra
import domain.users.UserRepositoryAlgebra
import doobie.util.transactor.Transactor

class AuthClient[F[_]: Sync : UserRepositoryAlgebra : TokenRepositoryAlgebra]
extends Http4sDsl[F] with Http4sClientDsl[F] {
  val userService = implicitly[UserRepositoryAlgebra[F]]
  val tokenService = implicitly[TokenRepositoryAlgebra[F]]

  val authEndpoints: AuthEndpoints[F, BCrypt] = new AuthEndpoints[F, BCrypt](BCrypt)
  val auth = authEndpoints.endpoints.orNotFound

  def getAuthHeaders(from: Response[F]) : Headers =
    from.headers.filter(_.name.toString startsWith "Authorization")

  def injectAuthHeader(from: Response[F])(to: Request[F]): Request[F] =
    to.withHeaders(getAuthHeaders(from))

  def lookupOrThrow(req: F[Request[F]]): F[Response[F]] = req.flatMap(authEndpoints.endpoints.orNotFound run _)

  def threadResponse(resp: Response[F])(req: Request[F]): F[Response[F]] = {
    val sessionReq = req.withHeaders(resp.headers.filter(_.name.toString.startsWith("Authorization")))
    auth.run(sessionReq)
  }

  def deleteUser(username: String): F[Unit] = (for {
    u <- userService.byUsername(username)
    (_, uid, _) = u
    _ <- OptionT.liftF(userService.delete(uid))
  } yield ()).getOrElse(())

  def postUser(userRequest: UserRequest): F[Response[F]] = lookupOrThrow(POST(uri("/user"), userRequest))

  def loginUser(userRequest: UserRequest): F[Response[F]] = lookupOrThrow(POST(uri("/login"), userRequest))

  // For the client, simply thread the most recent response back into any request that needs
  // authorization. There should probably be a better way to do this, maybe state monad or something.
  def getUser(userName: String, continue: Response[F]): Either[ParseFailure, F[Response[F]]] =
    Uri.fromString(s"/user/$userName").map(uri => GET(uri).flatMap(threadResponse(continue)(_)))

  def getUser(headers : Headers) : F[UserDetail] = for {
    req <- GET(uri("/user"))
    res <- auth.run(req.withHeaders(headers))
    userDetail <- res.as[UserDetail]
  } yield userDetail

  def withUser[A](u : UserRequest)(f : Headers => F[A]) : F[A] = for {
    register <- postUser(u)
    login <- loginUser(u)
    result <- f(getAuthHeaders(login))
    _ <- deleteUser(u.username)
  } yield result
}

object AuthClient {
  def fromTransactor[F[_] : Sync](xa : Transactor[F]) : AuthClient[F] = {
    implicit val userService = new UserRepositoryInterpreter[F](xa)
    implicit val tokenService = new TokenRepositoryInterpreter[F](xa)
    new AuthClient[F]
  }
}
