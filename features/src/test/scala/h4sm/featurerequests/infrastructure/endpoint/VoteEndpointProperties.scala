package h4sm.featurerequests
package infrastructure.endpoint

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import arbitraries._
import cats.effect.IO
import h4sm.auth.infrastructure.endpoint.UserRequest
import db.sql.testTransactor.testTransactor
import domain.requests._


class VoteEndpointProperties extends Properties("VoteEndpoint") {
  val eps = new TestRequests[IO](testTransactor)
  import eps._
  import authTestEndpoints._

  property("vote can be submitted") = forAll { (feat : FeatureRequest, user : UserRequest) =>
    val test : IO[Boolean] = for {
      _ <- postUser(user)
      login <- loginUser(user)
      _ <- addRequest(feat)(login)
      featuresResp <- getRequests
      allFeatures <- featuresResp.as[DefaultResult[List[VotedFeatures]]]
    } yield {
      allFeatures.result.exists(_.feature.title == feat.title)
    }

    test.unsafeRunSync()
  }
}
