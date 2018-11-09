package h4sm.files
package db.sql

import cats.effect.IO
import org.scalatest._
import h4sm.dbtesting.arbitraries._
import doobie.scalatest.IOChecker
import doobie.Transactor
import infrastructure.testTransactor
import files._
import h4sm.files.domain.arbitraries._

class FileQueriesTestSpec extends FunSuite with IOChecker {
  override def transactor: Transactor[IO] = testTransactor

  test("insert should typecheck")(check(applyArb(insert _)))
  test("select by id should typecheck")(check(applyArb(selectById _)))
  test("select user files should typecheck")(check(applyArb(selectFiles _)))
  test("update upload time should typecheck")(check(applyArb(updateFileUploadTime _)))
  test("delete by id should typecheck")(check(applyArb(deleteById _)))
}
