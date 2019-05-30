package h4sm
package featurerequests.db
package sql

import arbitraries._
import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import dbtesting.arbitraries._
import dbtesting.DbFixtureSuite
import votes._

class VoteSQLTestSpec extends DbFixtureSuite with IOChecker {
  val schemaNames = List("ct_auth", "ct_feature_requests")
  def transactor: Transactor[IO] = dbtesting.transactor.getTransactor(cfg)

  test("insert typechecks")(_ => check(applyArb(insert _)))
}
