package h4sm
package permissions.infrastructure.repository.persistent.sql


import doobie.scalatest.IOChecker
import org.scalatest._
import dbtesting.arbitraries._


import arbitraries._
import transactor._

class UserPermissionQueriesTestSpec extends FunSuite with IOChecker {
  implicit val transactor = testTransactor

  test("insert query should typecheck")(check(applyArb(userPermissions.insert _)))
  test("userPermission query should typecheck")(check(applyArb((userPermissions.userPermission _).tupled)))
  test("select should typecheck")(check(userPermissions.select))
  test("byId should typecheck")(check(applyArb(userPermissions.byId _)))
  test("delete should typecheck")(check(applyArb(userPermissions.delete _)))
}
