package h4sm
package files
package db.sql

import cats.effect.IO
import db.sql.files._
import dbtesting.arbitraries._
import dbtesting.DbFixtureSuite
import domain.arbitraries._
import doobie.scalatest.IOChecker
import doobie.Transactor

class FileQueriesTestSpec extends DbFixtureSuite with IOChecker {
  def schemaNames = List("ct_auth", "ct_files")
  def transactor: Transactor[IO] = dbtesting.transactor.getTransactor[IO](cfg)

  test("insert should typecheck")(_ => check(applyArb(insert _)))
  test("select by id should typecheck")(_ => check(applyArb(selectById _)))
  test("select user files should typecheck")(_ => check(applyArb(selectFiles _)))
  test("update upload time should typecheck")(_ => check(applyArb(updateFileUploadTime _)))
  test("delete by id should typecheck")(_ => check(applyArb(deleteById _)))
}
