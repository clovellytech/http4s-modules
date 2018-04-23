package com.clovellytech.featurerequests
package infrastructure.endpoint

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import arbitraries._
import cats.effect.IO
import com.clovellytech.auth.infrastructure.endpoint.UserRequest
import db.sql.testTransactor.testTransactor
import domain.requests._


class VoteEndpointProperties extends Properties("VoteEndpoint") {
  val eps = new TestRequests[IO](testTransactor)
  import eps._
  import authTestEndpoints._

  property("vote can be submitted") = forAll { (feat : FeatureRequest, user : UserRequest) =>
    val test : IO[Boolean] = for {
      register <- postUser(user)
      login <- loginUser(user)
      addRes <- addRequest(feat)(login)
      featuresResp <- getRequests
      allFeatures <- featuresResp.as[DefaultResult[List[VotedFeatures]]]
    } yield {
      allFeatures.result.exists(_.feature.title == feat.title)
    }

    test.unsafeRunSync()
  }
}
