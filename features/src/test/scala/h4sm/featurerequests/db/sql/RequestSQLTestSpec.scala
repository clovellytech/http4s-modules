package h4sm.featurerequests
package db
package sql

import org.scalatest._

import cats.effect.IO
import doobie.scalatest.IOChecker

import h4sm.dbtesting.arbitraries._
import arbitraries._
import requests._

class RequestSQLTestSpec extends FlatSpec with Matchers with IOChecker {

  val transactor: doobie.Transactor[IO] = testTransactor.testTransactor

  "Request queries" should "typecheck" in {
    check(applyArb(insert _))
    check(selectAll)
    check(selectAllWithVoteCounts)
  }
}
