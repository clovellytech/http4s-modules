package h4sm
package invitations.infrastructure.repository.persistent.sql

import arbitraries._
import auth.db.sql.arbitraries._
import cats.effect.IO
import testutil.arbitraries._
import testutil.DbFixtureSuite
import doobie.scalatest.IOChecker

class InvitationQueriesTestSpec extends DbFixtureSuite with IOChecker {
  def schemaNames = List("ct_auth", "ct_invitations")
  def transactor = dbtesting.transactor.getTransactor[IO](cfg)

  test("select query should typecheck")(_ => check(invitation.all))
  test("byId query should typecheck")(_ => check(applyArb(invitation.byId _)))
  test("delete should typecheck")(_ => check(applyArb(invitation.delete _)))
  test("insert should typecheck")(_ => check(applyArb(invitation.insert _)))
  test("fromToEmail should typecheck")(_ => check(applyArb(invitation.fromToEmail _)))
  test("byCode should typecheck")(_ => check(applyArb((invitation.byCode _).tupled)))
  test("updateOpenTime")(_ => check(applyArb(invitation.updateOpenTime _)))
}
