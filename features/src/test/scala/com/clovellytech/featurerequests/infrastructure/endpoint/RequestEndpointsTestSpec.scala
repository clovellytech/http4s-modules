package com.clovellytech.featurerequests
package infrastructure
package endpoint

import cats.data.OptionT
import org.scalatest._
import cats.effect.IO
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
//
//  testIO("Request lookup"){
//    val r =  for {
//      addRes <- addRequest(FeatureRequest("test2", "example"))
//      all <- getRequests()
//    } yield {
//      all.map(_.as[DefaultResult[List[VotedFeatures]]])
//    }

//  res.result.map(_.feature).filter(x => x.title == myFeature.title && x.description == myFeature.description) should not be empty

// }
}
