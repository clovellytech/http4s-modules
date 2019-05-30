package h4sm
package featurerequests.db
package sql

import arbitraries._
import cats.effect.IO
import dbtesting.arbitraries._
import dbtesting.DbFixtureBeforeAfter
import doobie.scalatest.IOChecker
import org.scalatest.FunSuite
import requests._


class RequestSQLTestSpec extends FunSuite with DbFixtureBeforeAfter with IOChecker {
  val schemaNames = List("ct_auth", "ct_feature_requests")
  def transactor: doobie.Transactor[IO] = dbtesting.transactor.getTransactor[IO](cfg)

  test("insert typechecks")(check(applyArb(insert _)))
  test("select typechecks")(check(select))
  test("selectAllWithVoteCounts typechecks")(check(selectAllWithVoteCounts))
  test("selectById typechecks")(check(applyArb(selectById _)))
}
