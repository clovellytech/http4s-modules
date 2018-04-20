package com.clovellytech.auth.db
package sql

import java.util.UUID

import cats.effect.IO
import com.clovellytech.auth.infrastructure.testTransactor
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import org.scalatest.FunSuite
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

class TokenQueriesTestSpec extends FunSuite with IOChecker {
  val transactor: Transactor[IO] = testTransactor

  import tokens._

  val time = java.time.Instant.MAX
  val t = TSecBearerToken(SecureRandomId.apply("123"), UUID.randomUUID(), time, Some(time))

  test("insert should typecheck")(check(insert(t)))
  test("select should typecheck")(check(select))
  test("select by id should typecheck")(check(byId(t.id)))
  test("select by user id should typecheck")(check(byUserId(t.identity)))
  test("update should typecheck")(check(update(t.id, t)))
  test("delete should typecheck")(check(delete(t.id)))
}
