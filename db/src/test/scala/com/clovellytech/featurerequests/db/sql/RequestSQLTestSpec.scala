package com.clovellytech.featurerequests.db
package sql

import java.util.UUID

import cats.effect.IO
import com.clovellytech.featurerequests.db.domain.Feature
import doobie.scalatest.IOChecker
import org.joda.time.DateTime
import org.scalatest._

import requests._

class RequestSQLTestSpec extends FlatSpec with Matchers with IOChecker {

  val transactor: doobie.Transactor[IO] = testTransactor

  "Request queries" should "typecheck" in {
    check(insert(Feature(UUID.randomUUID(), "Title", "Feature description", DateTime.now())))
  }
}