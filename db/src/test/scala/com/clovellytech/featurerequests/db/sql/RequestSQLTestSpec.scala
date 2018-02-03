package com.clovellytech.featurerequests.db
package sql

import org.scalatest._

import java.util.UUID

import cats.effect.IO
import cats.syntax.option._

import com.clovellytech.featurerequests.db.domain.Feature
import doobie.scalatest.IOChecker

import requests._

class RequestSQLTestSpec extends FlatSpec with Matchers with IOChecker {

  val transactor: doobie.Transactor[IO] = testTransactor.testTransactor

  "Request queries" should "typecheck" in {
    check(insert(Feature(UUID.randomUUID().some, "Title", "Feature description")))
    check(selectAll)
    check(selectAllWithVoteCounts)
  }
}