package h4sm
package permissions.infrastructure.repository.persistent.sql

import arbitraries._
import cats.effect.IO
import dbtesting.arbitraries._
import dbtesting.DbFixtureSuite
import doobie.scalatest.IOChecker

class UserPermissionQueriesTestSpec extends DbFixtureSuite with IOChecker {
  def schemaNames = List("ct_auth", "ct_permissions")
  def transactor = dbtesting.transactor.getTransactor[IO](cfg)

  test("insert query should typecheck")(_ => check(applyArb(userPermissions.insert _)))
  test("userPermission query should typecheck")(_ => check(applyArb((userPermissions.userPermission _).tupled)))
  test("select should typecheck")(_ => check(userPermissions.select))
  test("byId should typecheck")(_ => check(applyArb(userPermissions.byId _)))
  test("delete should typecheck")(_ => check(applyArb(userPermissions.delete _)))
}
