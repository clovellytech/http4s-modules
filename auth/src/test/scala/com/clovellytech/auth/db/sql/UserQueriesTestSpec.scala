package com.clovellytech.auth.db
package sql

import java.util.UUID

import cats.effect.IO
import org.scalatest.FunSuite
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor

import domain._
import com.clovellytech.auth.infrastructure.testTransactor

class UserQueriesTestSpec extends FunSuite with IOChecker {
  val transactor: Transactor[IO] = testTransactor

  import users._

  val u = User("name", "hash".getBytes())
  val uuid = UUID.randomUUID()

  test("insert should typecheck")(check(insert(u)))
  test("select should typecheck")(check(select))
  test("select by id should typecheck")(check(selectById(uuid)))
  test("update should typecheck")(check(update(uuid, u)))
  test("delete should typecheck")(check(delete(uuid)))
}
