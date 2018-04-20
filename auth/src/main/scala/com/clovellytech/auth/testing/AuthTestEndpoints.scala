package com.clovellytech.auth
package testing

import cats.implicits._
import cats.effect.Sync
import com.clovellytech.auth.infrastructure.endpoint.{AuthEndpoints, UserRequest}
import doobie.util.transactor.Transactor
import org.http4s._
import org.http4s.dsl._
import org.http4s.client.dsl._
import tsec.passwordhashers.jca.BCrypt
import domain.tokens.TokenService
import domain.users.UserService
import infrastructure.repository.persistent.{TokenRepositoryInterpreter, UserRepositoryInterpreter}


class AuthTestEndpoints[F[_]: Sync](xa: Transactor[F]) extends Http4sDsl[F] with Http4sClientDsl[F] {
  val authEndpoints: AuthEndpoints[F, BCrypt] = {
    val uinterp = new UserRepositoryInterpreter[F](xa)
    val uservice = new UserService[F](uinterp)
    val tinterp = new TokenRepositoryInterpreter[F](xa)
    val tservice = new TokenService[F](tinterp)
    new AuthEndpoints[F, BCrypt](uservice, tservice, BCrypt)
  }

  def auth(req : F[Request[F]]) = req.flatMap(authEndpoints.endpoints.orNotFound run _)

  def authFrom(resp : Response[F])(req : Request[F]): F[Response[F]] = {
    val sessionReq = req.withHeaders(resp.headers.filter(_.name.toString.startsWith("Authorization")))
    authEndpoints.endpoints.orNotFound run sessionReq
  }

  def postUser(userRequest: UserRequest): F[Response[F]] = auth(POST(uri("/user"), userRequest))

  def loginUser(userRequest: UserRequest): F[Response[F]] = auth(POST(uri("/login"), userRequest))


  // For the client, simply thread the most recent response back into any request that needs
  // authorization. There should probably be a better way to do this, maybe state monad or something.
  def getUser(userName : String, continue : Response[F]): Either[ParseFailure, F[Response[F]]] =
    Uri.fromString(s"/user/$userName").map(uri => GET(uri).flatMap(authFrom(continue)(_)))
}
