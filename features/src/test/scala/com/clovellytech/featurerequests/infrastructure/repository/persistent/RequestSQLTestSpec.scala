package com.clovellytech.featurerequests
package infrastructure.repository.persistent


import java.util.UUID

import cats.effect.IO
import org.scalatest._
import doobie.scalatest._
import domain.requests._
import org.joda.time.DateTime



class RequestSQLTestSpec extends FlatSpec with Matchers with IOChecker {
  import RequestSQL._

  val transactor: doobie.Transactor[IO] = testTransactor

  "Request queries" should "typecheck" in {
    check(insert(Feature(UUID.randomUUID(), "Title", "Feature description", DateTime.now())))
  }
}