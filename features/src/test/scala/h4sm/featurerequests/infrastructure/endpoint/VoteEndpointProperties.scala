package h4sm.featurerequests
package infrastructure.endpoint

import arbitraries._
import cats.effect.IO
import domain.requests._
import h4sm.auth.infrastructure.endpoint.UserRequest
import h4sm.dbtesting.DbFixtureSuite
import h4sm.featurerequests.db.domain.VotedFeature
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class VoteEndpointProperties extends ScalaCheckPropertyChecks with DbFixtureSuite {
  def schemaNames = Seq("ct_auth", "ct_feature_requests")

  test("vote can be submitted prop") { p : FixtureParam =>
    val reqs = new TestRequests[IO](p.transactor)
    import reqs._
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
