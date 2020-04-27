package h4sm
package messages
package infrastructure.repository.persistent.sql

import h4sm.messages.domain.arbitraries._
import cats.effect.IO
import h4sm.testutil.arbitraries._
import h4sm.testutil.DbFixtureBeforeAfter
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import org.scalatest.funsuite.AnyFunSuite

class MessageSqlTestSpec extends AnyFunSuite with DbFixtureBeforeAfter with IOChecker {
  def schemaNames: Seq[String] = List("ct_auth", "ct_messages")
  def transactor: Transactor[IO] = testutil.transactor.getTransactor[IO](cfg)

  test("insert should typecheck")(check(applyArb(messages.insert(_))))
  test("select should typecheck")(check(messages.select))
  test("byId should typecheck")(check(applyArb(messages.byId _)))
  test("delete should typecheck")(check(applyArb(messages.delete _)))
  test("inbox should typecheck")(check(applyArb(messages.inbox _)))
  test("thread should typecheck")(check(applyArb((messages.thread _).tupled)))
}
