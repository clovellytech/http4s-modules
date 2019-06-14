package h4sm.petstore
package db.sql

import cats.effect.IO
import h4sm.db.config.DatabaseConfig
import h4sm.dbtesting.arbitraries._
import h4sm.dbtesting.DbFixtureSuite
import h4sm.dbtesting.transactor.getTransactor
import doobie.scalatest.IOChecker
import doobie.Transactor
import domain.arbitraries._

class PetQueriesTestSpec extends DbFixtureSuite with IOChecker {
  override def testConfig: DatabaseConfig = config.copy(databaseName = "petstoretest") // only needed for documentation
  override def colors = doobie.util.Colors.None // only needed for documentation
  def schemaNames = List("ct_auth", "ct_files", "ct_permissions", "ct_petstore")
  def transactor: Transactor[IO] = getTransactor[IO](cfg)

  test("delete should typecheck")(_ => check(applyArb(pets.delete _)))
  test("insert should typecheck")(_ => check(applyArb(pets.insert _)))
  test("select should typecheck")(_ => check(pets.select))
  test("select by id should typecheck")(_ => check(applyArb(pets.selectById _)))
  test("select by name typecheck")(_ => check(applyArb(pets.selectByName _)))
  test("update upload time should typecheck")(_ => check(applyArb((pets.safeUpdate _).tupled)))
}
