package h4sm
package petstore
package infrastructure.endpoint

import cats.effect.IO
import testutil.DbFixtureSuite
import auth.client.IOTestAuthClientChecks
import auth.infrastructure.endpoint.arbitraries._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.matchers.should.Matchers
import petstore.infrastructure.endpoint.arbitraries._
import petstore.client.PetstoreClientRunner

class OrderEndpointsProperties
    extends DbFixtureSuite
    with IOTestAuthClientChecks
    with ScalaCheckPropertyChecks
    with Matchers {
  def schemaNames: Seq[String] = List("ct_auth", "ct_permissions", "ct_files", "ct_petstore")

  test("Create order endpoint") { p =>
    new PetstoreClientRunner[IO] {
      val xa = p.transactor

      forAnyUser2(testAuthClient) { implicit headers => (_, p: PetRequest) =>
        for {
          _ <- petstoreClient.addPet(p)
          allpets <- pets.select
          insertedPetId = allpets.find(_._1.name == p.name).map(_._2).get
          _ <- petstoreClient.orderPet(OrderRequest(insertedPetId))
          allorders <- orders.select
          _ <- pets.delete(insertedPetId)
        } yield {
          allorders.map(_._1.petId) should contain(insertedPetId)
        }
      }
    }
  }
}
