package h4sm.petstore
package db.sql

import cats.effect.IO
import h4sm.testutil.arbitraries._
import h4sm.testutil.DbFixtureSuite
import h4sm.testutil.transactor.getTransactor
import doobie.scalatest.IOChecker
import doobie.Transactor
import domain.arbitraries._

class OrderQueriesTestSpec extends DbFixtureSuite with IOChecker {
  def schemaNames = List("ct_auth", "ct_files", "ct_permissions", "ct_petstore")
  def transactor: Transactor[IO] = getTransactor[IO](cfg)

  test("delete should typecheck")(_ => check(applyArb(orders.delete _)))
  test("insert should typecheck")(_ => check(applyArb(orders.insert _)))
  test("select by id should typecheck")(_ => check(applyArb(orders.selectById _)))
  test("set shipped")(_ => check(applyArb(orders.setShipped _)))
}
