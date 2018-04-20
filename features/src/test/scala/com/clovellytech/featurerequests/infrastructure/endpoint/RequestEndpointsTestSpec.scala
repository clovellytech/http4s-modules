package com.clovellytech.featurerequests
package infrastructure
package endpoint

import cats.data.OptionT
import org.scalatest._
import cats.effect.IO
import com.clovellytech.auth.infrastructure.endpoint.IOTest
import domain.requests._
import db.sql.testTransactor.testTransactor
import org.http4s.Response
import db.sql.testTransactor.testTransactor

class RequestEndpointsTestSpec extends FunSuite with IOTest with Matchers {
  val eps = new TestRequests[IO](testTransactor)
  import eps._

  testIO("new request") {
    val req: IO[OptionT[IO, Response[IO]]] = addRequest(FeatureRequest("hi", "do this"))

    val test: IO[OptionT[IO, Assertion]] = req.map(_.map(_.status.code should equal (200)))

    test.map(_.getOrElse(fail("Request failed")))
  }
}
