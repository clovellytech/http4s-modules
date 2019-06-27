package h4sm
package petstore
package infrastructure.endpoint

import arbitraries._
import cats.effect.IO
import client.PetstoreClient
import testutil.DbFixtureSuite
import auth.client.IOTestAuthClientChecks
import auth.client.TestAuthClient
import auth.client.AuthClient
import auth.domain.UserService
import auth.infrastructure.endpoint._
import auth.infrastructure.endpoint.arbitraries._
import auth.infrastructure.repository.persistent._
import auth.UserAuthenticator
import infrastructure.repository.persistent._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.Matchers
import tsec.authentication.TSecBearerToken
import tsec.authentication.SecuredRequestHandler
import tsec.passwordhashers.jca.BCrypt

class PetEndpointsProperties extends DbFixtureSuite with IOTestAuthClientChecks with ScalaCheckPropertyChecks with Matchers {
  def schemaNames: Seq[String] = List("ct_auth", "ct_permissions", "ct_files", "ct_petstore")

  test("Create pet endpoint"){ p =>
    implicit val pets = new PetRepository(p.transactor)

    implicit val userAlg = new UserRepositoryInterpreter(p.transactor)
    val userService = new UserService[IO, BCrypt](BCrypt)
    implicit val ts = new TokenRepositoryInterpreter(p.transactor)
    implicit val os = new OrderRepository(p.transactor)
    val auth: UserAuthenticator[IO, TSecBearerToken] = Authenticators.bearer[IO]
    
    val ac = new AuthClient[IO, BCrypt, TSecBearerToken](userService, auth)
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
