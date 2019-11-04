package h4sm
package store
package infrastructure.repository.sql

import arbitraries._
import auth.db.sql.arbitraries._
import cats.effect.IO
import testutil.arbitraries._
import testutil.DbFixtureSuite
import doobie.scalatest.IOChecker

class ItemSqlTestSpec extends DbFixtureSuite with IOChecker {
  def schemaNames = List("ct_auth", "ct_store")
  def transactor = testutil.transactor.getTransactor[IO](cfg)
  
  test("select should typecheck")(_ => check(item.select))
  test("byId should typecheck")(_ => check(applyArb(item.byId _)))
  test("byIds should typecheck")(_ => check(applyArb(item.byIds _)))
  test("delete should typecheck")(_ => check(applyArb(item.delete _)))
  test("insert should typecheck")(_ => check(applyArb(item.insert _)))
  test("update should typecheck")(_ => check(applyArb((item.update _).tupled)))
}