package h4sm
package petstore
package client

import auth.client.AuthClientRunner
import cats.effect.{Bracket, Sync}
import infrastructure.endpoint._
import infrastructure.repository.persistent._
import tsec.authentication.SecuredRequestHandler
import tsec.authentication.TSecBearerToken

abstract class PetstoreClientRunner[F[_]: Bracket[?[_], Throwable]: Sync]
    extends AuthClientRunner[F] {
  implicit lazy val pets = new PetRepository(xa)
  implicit lazy val orders = new OrderRepository(xa)

  lazy val petEndpoints = new PetEndpoints[F, TSecBearerToken](SecuredRequestHandler(auth))
  lazy val orderEndpoints = new OrderEndpoints[F, TSecBearerToken](SecuredRequestHandler(auth))
  lazy val petstoreClient = new PetstoreClient[F, TSecBearerToken](petEndpoints, orderEndpoints)
}
