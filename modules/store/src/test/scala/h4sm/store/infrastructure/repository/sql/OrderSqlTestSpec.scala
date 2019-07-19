package h4sm
package store.infrastructure.repository.sql

import arbitraries._
import auth.db.sql.arbitraries._
import cats.effect.IO
import testutil.arbitraries._
import testutil.DbFixtureSuite
import doobie.scalatest.IOChecker

class OrderQueriesTestSpec extends DbFixtureSuite with IOChecker {
  def schemaNames = List("ct_auth", "ct_store")
  def transactor = testutil.transactor.getTransactor[IO](cfg)

  test("select query should typecheck")(_ => check(order.select))
  test("insert should typecheck")(_ => check(applyArb(order.insert _)))
  test("insert order item should typecheck")(_ => check(applyArb((order.insertOrderItem _).tupled)))
  test("update order should typecheck")(_ => check(applyArb((order.update _).tupled)))
  test("setSubmitted should typecheck")(_ => check(applyArb(order.setSubmitted _)))
}
