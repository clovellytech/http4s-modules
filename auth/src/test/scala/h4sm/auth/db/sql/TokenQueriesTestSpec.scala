package h4sm
package auth.db
package sql

import arbitraries._
import cats.effect.IO
import dbtesting.arbitraries._
import dbtesting.DbFixtureBeforeAfter
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import org.scalatest.FunSuite

class TokenQueriesTestSpec extends FunSuite with DbFixtureBeforeAfter with IOChecker {
  def schemaNames: Seq[String] = List("ct_auth")
  def transactor: Transactor[IO] = dbtesting.transactor.getTransactor[IO](cfg)

  import tokens._

  test("insert should typecheck")(check(applyArb(insert)))
  test("select should typecheck")(check(select))
  test("select by id should typecheck")(check(applyArb(byId)))
  test("select by user id should typecheck")(check(applyArb(byUserId)))
  test("update should typecheck")(check(applyArb((update _).tupled)))
  test("delete should typecheck")(check(applyArb(delete)))
}
