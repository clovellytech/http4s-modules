package h4sm
package petstore
package infrastructure.endpoint

import arbitraries._
import auth.client.IOTestAuthClientChecks
import auth.comm.arbitraries._
import cats.effect.IO
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.matchers.should.Matchers
import petstore.client.PetstoreClientRunner
import testutil.DbFixtureSuite

class PetEndpointsProperties
    extends DbFixtureSuite
    with IOTestAuthClientChecks
    with ScalaCheckPropertyChecks
    with Matchers {
  def schemaNames: Seq[String] = List("ct_auth", "ct_permissions", "ct_files", "ct_petstore")

  test("Create pet endpoint") { p =>
    new PetstoreClientRunner[IO] {
      val xa = p.transactor
      forAnyUser2(testAuthClient) { implicit headers => (_, p: PetRequest) =>
        for {
          _ <- petstoreClient.addPet(p)
          ls <- pets.select
          _ <- ls
            .collectFirst { case (pet, id, _) if pet.name == p.name => pets.delete(id) }
            .getOrElse(IO(()))
        } yield {
          ls.map(_._1.name) should contain(p.name)
        }
      }
    }
  }
}
