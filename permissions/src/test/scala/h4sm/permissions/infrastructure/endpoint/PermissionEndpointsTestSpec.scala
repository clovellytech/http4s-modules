package h4sm
package permissions
package infrastructure
package endpoint

import auth.infrastructure.endpoint._
import cats.effect.IO
import h4sm.auth.client.{AuthClient, IOTestAuthClient}
import h4sm.permissions.infrastructure.repository.{UserPermissionRepository, PermissionRepository}
import permissions.domain._
import org.scalatest._
import org.scalatest.prop.PropertyChecks
import tsec.passwordhashers.jca.BCrypt
import repository.persistent.sql.transactor._
import repository.persistent.sql.arbitraries._

class PermissionEndpointsTestSpec extends FlatSpec with Matchers with PropertyChecks with IOTestAuthClient {
  val authEndpoints = AuthEndpoints.persistingEndpoints(testTransactor)
  implicit val permRepo = new PermissionRepository[IO](testTransactor)
  implicit val userPermRepo = new UserPermissionRepository[IO](testTransactor)
  val userRepo = authEndpoints.userService
  val permissionEndpoints = new PermissionEndpoints[IO, BCrypt](authEndpoints)
  val authClient = AuthClient.fromTransactor(testTransactor)
  val permissionClient = new PermissionClient[IO, BCrypt](permissionEndpoints)

  def permitUser(ur : UserRequest, p : Permission) : IO[PermissionId] = for {
    userDetails <- userRepo.byUsername(ur.username).getOrElse(fail)
    permId <- permRepo.selectByAttributes(p.appName, p.name).map(_._2).orElse(permRepo.insertGetId(p)).getOrElse(fail)
    _ <- userPermRepo.insert(UserPermission(userDetails._2, permId, userDetails._2))
  } yield permId

  "a logged in non admin user" should "not be able to add a permission" in forAnyUser2 { implicit h =>
    (_ : UserRequest, p : Permission) =>
      permissionClient.addPermission(p).attempt.map(_.isLeft should equal (true))
  }

  "a logged in permissioned user" should "be able to add a permission" in forAnyUser2 { implicit h =>
    (ur : UserRequest, p : Permission) =>
      val program = for {
        pid <- permitUser(ur, Permission("admin", "", "ct_permissions"))
        _ <- permissionClient.addPermission(p)
        _ <- permRepo.delete(pid)
      } yield ()

      program.attempt.map(_.isRight should equal (true))
  }
}