package h4sm.featurerequests.db
package sql

import cats.effect.IO
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import org.scalatest._
import votes._
import h4sm.dbtesting.arbitraries._
import arbitraries._

class VoteSQLTestSpec extends FlatSpec with Matchers with IOChecker {
  val transactor: Transactor[IO] = testTransactor.testTransactor

  "Vote SQL" should "typecheck" in {
    check(applyArb(insert _))
  }
}
