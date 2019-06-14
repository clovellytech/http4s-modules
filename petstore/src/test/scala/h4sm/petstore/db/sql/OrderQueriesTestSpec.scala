package h4sm.petstore
package db.sql

import cats.effect.IO
import h4sm.dbtesting.arbitraries._
import h4sm.dbtesting.DbFixtureSuite
import h4sm.dbtesting.transactor.getTransactor
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
