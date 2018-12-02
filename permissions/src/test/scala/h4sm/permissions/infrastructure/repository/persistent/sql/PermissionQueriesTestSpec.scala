package h4sm
package permissions.infrastructure.repository
package persistent.sql

import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import org.scalatest._
import org.scalacheck._

import dbtesting.arbitraries._
import arbitraries._

class PermissionQueriesTestSpec extends FunSuite with IOChecker {
  def transactor: Transactor[IO] = persistent.sql.transactor.testTransactor

  implicit val stringArb = Arbitrary(nonEmptyString)

  test("select should typecheck")(check(permissions.select))
  test("select by id should typecheck")(check(applyArb(permissions.byId _)))
  test("select by app name should typecheck")(check(applyArb(permissions.byAppName _)))
  test("select by atttributes should typecheck")(check(applyArb((permissions.byAttributes _).tupled)))
  test("insert should typecheck")(check(applyArb(permissions.insert _)))
  test("safe update should typecheck")(check(applyArb((permissions.safeUpdate _).tupled)))
  test("delete should typecheck")(check(applyArb(permissions.delete _)))
}
