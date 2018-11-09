package h4sm.auth.db
package sql

import java.util.UUID

import cats.effect.IO
import org.scalatest.FunSuite
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor

import domain._
import h4sm.auth.infrastructure.testTransactor

import h4sm.dbtesting.arbitraries._
import arbitraries._

class UserQueriesTestSpec extends FunSuite with IOChecker {
  val transactor: Transactor[IO] = testTransactor

  import users._

  val u = User("name", "hash".getBytes())
  val uuid = UUID.randomUUID()

  test("insert should typecheck")(check(applyArb(insert _)))
  test("select should typecheck")(check(select))
  test("select by id should typecheck")(check(applyArb(selectById _)))
  test("update should typecheck")(check(applyArb((update _).tupled)))
  test("delete should typecheck")(check(applyArb(delete _)))
}
