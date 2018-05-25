package com.clovellytech.auth.db
package sql

import cats.effect.IO
import com.clovellytech.auth.infrastructure.testTransactor
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import org.scalatest.FunSuite
import com.clovellytech.dbtesting.arbitraries._
import arbitraries._

class TokenQueriesTestSpec extends FunSuite with IOChecker {
  val transactor: Transactor[IO] = testTransactor

  import tokens._

  test("insert should typecheck")(check(applyArb(insert)))
  test("select should typecheck")(check(select))
  test("select by id should typecheck")(check(applyArb(byId)))
  test("select by user id should typecheck")(check(applyArb(byUserId)))
  test("update should typecheck")(check(applyArb((update _).tupled)))
  test("delete should typecheck")(check(applyArb(delete)))
}
