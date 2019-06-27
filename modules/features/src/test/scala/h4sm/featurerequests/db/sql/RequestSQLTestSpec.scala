package h4sm
package featurerequests.db
package sql

import arbitraries._
import testutil.arbitraries._
import testutil.TypeCheckTestSpec
import requests._


class RequestSQLTestSpec extends TypeCheckTestSpec {
  val schemaNames = List("ct_auth", "ct_feature_requests")

  test("insert typechecks")(check(applyArb(insert _)))
  test("select typechecks")(check(select))
  test("selectAllWithVoteCounts typechecks")(check(selectAllWithVoteCounts))
  test("selectById typechecks")(check(applyArb(selectById _)))
}
