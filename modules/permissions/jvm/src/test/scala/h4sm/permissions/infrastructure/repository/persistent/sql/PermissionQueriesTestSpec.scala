package h4sm
package permissions.infrastructure.repository
package persistent.sql

import arbitraries._
import cats.effect.IO
import testutil.arbitraries._
import testutil.DbFixtureSuite
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import org.scalacheck._

class PermissionQueriesTestSpec extends DbFixtureSuite with IOChecker {
  def schemaNames = List("ct_auth", "ct_permissions")
  def transactor: Transactor[IO] = testutil.transactor.getTransactor[IO](cfg)

  implicit val stringArb = Arbitrary(nonEmptyString)

  test("select should typecheck")(_ => check(permissions.select))
  test("select by id should typecheck")(_ => check(applyArb(permissions.byId _)))
  test("select by app name should typecheck")(_ => check(applyArb(permissions.byAppName _)))
  test("select by atttributes should typecheck")(_ =>
    check(applyArb((permissions.byAttributes _).tupled)),
  )
  test("insert should typecheck")(_ => check(applyArb(permissions.insert _)))
  test("update should typecheck")(_ => check(applyArb((permissions.update _).tupled)))
  test("update unique should typecheck")(_ => check(applyArb((permissions.update _).tupled)))
  test("delete should typecheck")(_ => check(applyArb(permissions.delete _)))
}
