package h4sm.petstore
package infrastructure.endpoint

import arbitraries._
import cats.effect.IO
import client.PetstoreClient
import h4sm.dbtesting.DbFixtureSuite
import h4sm.auth.client.IOTestAuthClientChecks
import h4sm.auth.client.TestAuthClient
import h4sm.auth.client.AuthClient
import h4sm.auth.infrastructure.endpoint._
import h4sm.auth.infrastructure.endpoint.arbitraries._
import h4sm.auth.infrastructure.repository.persistent._
import h4sm.auth.UserAuthenticator
import infrastructure.repository.persistent._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.Matchers
import tsec.authentication.TSecBearerToken
import tsec.authentication.SecuredRequestHandler

class PetEndpointsProperties extends DbFixtureSuite with IOTestAuthClientChecks with ScalaCheckPropertyChecks with Matchers {
  def schemaNames: Seq[String] = List("ct_auth", "ct_permissions", "ct_files", "ct_petstore")

  test("Create pet endpoint"){ p =>
    implicit val pets = new PetRepository(p.transactor)

    implicit val us = new UserRepositoryInterpreter(p.transactor)
    implicit val ts = new TokenRepositoryInterpreter(p.transactor)
    implicit val os = new OrderRepository(p.transactor)
    val auth: UserAuthenticator[IO, TSecBearerToken] = Authenticators.bearer[IO]
    
    val ac = new AuthClient[IO, TSecBearerToken](auth)
    val tc = new TestAuthClient(ac)
    val pe = new PetEndpoints[IO, TSecBearerToken](SecuredRequestHandler(auth))
    val oe = new OrderEndpoints[IO, TSecBearerToken](SecuredRequestHandler(auth))
    val pc = new PetstoreClient[IO, TSecBearerToken](pe, oe)

    forAnyUser2(tc) { implicit headers => (_, p: PetRequest) =>
      for {
        _ <- pc.addPet(p)
        ls <- pets.select
        _ <- ls.collectFirst{ case (pet, id, _) if pet.name == p.name => pets.delete(id) }.getOrElse(IO(()))
      } yield {
        ls.map(_._1.name) should contain (p.name)
      }
    }
  }
}
