package h4sm
package permissions
package infrastructure
package endpoint

import auth.infrastructure.endpoint._
import cats.effect.IO
import doobie.Transactor
import h4sm.auth.client.{AuthClient, IOTestAuthClientChecks, TestAuthClient}
import h4sm.auth.domain.users.UserRepositoryAlgebra
import h4sm.auth.infrastructure.endpoint.arbitraries._
import h4sm.auth.infrastructure.repository.persistent.{TokenRepositoryInterpreter, UserRepositoryInterpreter}
import h4sm.db.config._
import h4sm.dbtesting.DbFixtureSuite
import h4sm.permissions.infrastructure.repository.{PermissionRepository, UserPermissionRepository}
import permissions.domain._
import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import tsec.passwordhashers.jca.BCrypt
import repository.persistent.sql.arbitraries._
import io.circe.config.parser
import tsec.authentication.TSecBearerToken
import h4sm.auth.domain.tokens.AsBaseTokenInstances._

class PermissionEndpointsTestSpec
extends Matchers
with ScalaCheckPropertyChecks
with DbFixtureSuite
with IOTestAuthClientChecks {

  def schemaNames: Seq[String] = Seq("ct_auth", "ct_permissions")
  def config: DatabaseConfig = parser.decodePathF[IO, DatabaseConfig]("db").unsafeRunSync()

  case class Clients(
    userRepo : UserRepositoryAlgebra[IO],
    permRepo : PermissionAlgebra[IO],
    userPermRepo : UserPermissionAlgebra[IO],
    testAuthClient : TestAuthClient[IO],
    permClient : PermissionClient[IO, BCrypt, TSecBearerToken])

  def clients(xa : Transactor[IO]) : Clients = {
    implicit val userService = new UserRepositoryInterpreter(xa)
    implicit val tokenService = new TokenRepositoryInterpreter(xa)
    val authEndpoints = new AuthEndpoints(BCrypt, Authenticators.bearer[IO])
    implicit val permRepo = new PermissionRepository[IO](xa)
    implicit val userPermRepo = new UserPermissionRepository[IO](xa)
    val userRepo = authEndpoints.userService
    val permissionEndpoints = new PermissionEndpoints[IO, BCrypt, TSecBearerToken](authEndpoints)
    val authClient = AuthClient.fromTransactor(xa)
    val permissionClient = new PermissionClient(permissionEndpoints)
    val testAuthClient = new TestAuthClient(authClient)
    Clients(userRepo, permRepo, userPermRepo, testAuthClient, permissionClient)
  }

  def permitUser(cs : Clients)(
    ur : UserRequest,
    p : Permission
  ) : IO[PermissionId] = for {
    userDetails <- cs.userRepo.byUsername(ur.username).getOrElse(fail)
    permId <- cs.permRepo.selectByAttributes(p.appName, p.name).map(_._2).orElse(cs.permRepo.insertGetId(p)).getOrElse(fail)
    _ <- cs.userPermRepo.insert(UserPermission(userDetails._2, permId, userDetails._2))
  } yield permId

  test("a logged in non admin user should not be able to add a permission") { p =>
    val cs = clients(p.transactor)

    forAnyUser2(cs.testAuthClient) { implicit h => (_: UserRequest, p: Permission) =>
      cs.permClient.addPermission(p).attempt.map(_.isLeft should equal(true))
    }
  }

  test("a logged in permissioned user be able to add a permission") { p =>
    val cs = clients(p.transactor)

    forAnyUser2(cs.testAuthClient) { implicit h => (ur: UserRequest, p: Permission) =>
      val program = for {
        pid <- permitUser(cs)(ur, Permission("admin", "", "ct_permissions"))
        _ <- cs.permClient.addPermission(p)
        _ <- cs.permRepo.delete(pid)
      } yield ()

      program.attempt.map(_.isRight should equal(true))
    }
  }

}