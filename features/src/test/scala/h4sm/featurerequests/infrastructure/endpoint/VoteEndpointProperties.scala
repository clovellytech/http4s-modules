package h4sm.featurerequests
package infrastructure.endpoint

import arbitraries._
import cats.effect.IO
import h4sm.auth.infrastructure.endpoint.UserRequest
import domain.requests._
import h4sm.featurerequests.db.domain.VotedFeature
import org.scalatest.prop.PropertyChecks


class VoteEndpointProperties extends PropertyChecks with DbFixtureSuite {
  val dbName = "vote_endpoints_test_property_spec"

  test("vote can be submitted prop") { p : FixtureParam =>
    import p.reqs._
    import authTestEndpoints._

    forAll { (feat: FeatureRequest, user: UserRequest) =>
      val test: IO[Boolean] = for {
        _ <- postUser(user)
        login <- loginUser(user)
        _ <- addRequest(feat)(login)
        featuresResp <- getRequests
        allFeatures <- featuresResp.as[DefaultResult[List[VotedFeature]]]
      } yield {
        allFeatures.result.exists(_.feature.title == feat.title)
      }

      test.unsafeRunSync()
    }
  }
}
