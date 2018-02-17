package com.clovellytech.featurerequests
package infrastructure
package endpoint

import org.scalatest._
import cats.data.OptionT
import cats.effect.IO
import domain.requests._


class RequestEndpointsTestSpec extends FunSuite with IOTest with Matchers {
  val eps = new TestRequests[IO]
  import eps._

  testIO("new request") {
    OptionT(addRequest(FeatureRequest("hi", "do this"))).map{ res =>
      res.status.code should equal (200)
    }.value.map(_ should not equal None)
  }

  testIO("Request lookup"){
    (for {
      myFeature <- OptionT.liftF(IO(FeatureRequest("test2", "example")))
      addRes <- OptionT(addRequest(myFeature))
      all <- OptionT(getRequests())
      res <- OptionT.liftF(all.as[DefaultResult[List[VotedFeatures]]])
    } yield {
      res.result.map(_.feature).filter(x => x.title == myFeature.title && x.description == myFeature.description) should not be empty
    }).value.map(_ should not equal None)
  }
}
