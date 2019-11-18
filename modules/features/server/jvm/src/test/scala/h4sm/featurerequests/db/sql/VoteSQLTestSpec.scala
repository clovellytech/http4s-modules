package h4sm
package featurerequests.db
package sql

import arbitraries._
import testutil.arbitraries._
import testutil.TypeCheckTestSpec
import votes._

class VoteSQLTestSpec extends TypeCheckTestSpec {
  val schemaNames = List("ct_auth", "ct_feature_requests")

  test("insert typechecks")(check(applyArb(insert _)))
}
