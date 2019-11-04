package h4sm
package permissions
package infrastructure
package endpoint

import auth.infrastructure.endpoint.arbitraries._
import auth.comm.UserRequest
import cats.data.OptionT
import cats.effect.IO
import auth.client.IOTestAuthClientChecks
import testutil.DbFixtureSuite
import permissions.client.PermissionClientRunner
import permissions.domain._
import org.http4s.Headers
import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import repository.persistent.sql.arbitraries._

class PermissionEndpointsTestSpec
extends Matchers
with ScalaCheckPropertyChecks
with DbFixtureSuite
with IOTestAuthClientChecks {

  def schemaNames: Seq[String] = Seq("ct_auth", "ct_permissions")

  test("a logged in non admin user should not be able to add a permission") { p =>
    new PermissionClientRunner[IO] {
      val xa = p.transactor      
      forAnyUser2(testAuthClient) { implicit h => (_: UserRequest, p: Permission) =>
        permissionClient.addPermission(p).attempt.map(_.isLeft should equal(true))
      }
    }
  }

  test("a logged in permissioned user be able to add a permission") { p =>
    new PermissionClientRunner[IO] {
      val xa = p.transactor
      
      forAnyUser2(testAuthClient) { implicit h: Headers => (ur: UserRequest, p: Permission) =>
        val program: OptionT[IO, Assertion] = for {
          pid <- permitUser(ur, Permission("admin", "", "ct_permissions"))
          _ <- OptionT.liftF(permissionClient.addPermission(p))
          _ <- OptionT.liftF(permRepo.delete(pid))
        } yield 1 should equal (1)  // todo - test something
  
        program.getOrElseF(fail("Add permission result returned none"))
      }  
    }
  }
}
