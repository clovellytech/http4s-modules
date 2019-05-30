package h4sm
package auth.db
package sql

import arbitraries._
import cats.effect.IO
import dbtesting.arbitraries._
import dbtesting.DbFixtureBeforeAfter
import domain._
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import java.util.UUID
import org.scalatest.FunSuite
import users._

class UserQueriesTestSpec extends FunSuite with DbFixtureBeforeAfter with IOChecker {
  def schemaNames: Seq[String] = List("ct_auth")
  val transactor: Transactor[IO] = dbtesting.transactor.getTransactor[IO](cfg)

  val u = User("name", "hash".getBytes())
  val uuid = UUID.randomUUID()

  test("insert should typecheck")(check(applyArb(insert _)))
  test("select should typecheck")(check(select))
  test("select by id should typecheck")(check(applyArb(selectById _)))
  test("update should typecheck")(check(applyArb((update _).tupled)))
  test("delete should typecheck")(check(applyArb(delete _)))
}
