package h4sm.featurerequests
package infrastructure.endpoint

import arbitraries._
import cats.effect.IO
import domain.requests._
import h4sm.auth.comm.UserRequest
import h4sm.testutil.DbFixtureSuite
import h4sm.featurerequests.db.domain.VotedFeature
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class RequestEndpointProperties extends ScalaCheckPropertyChecks with DbFixtureSuite {
  def schemaNames = Seq("ct_auth", "ct_feature_requests")

  test("request properties") { p =>
    val reqs = new TestRequests[IO](p.transactor)
    import reqs._
    import authTestEndpoints._

    forAll { (feat: FeatureRequest, u: UserRequest) =>
      val test: IO[Boolean] = for {
        _ <- postUser(u)
        login <- loginUser(u)
        _ <- addRequest(feat)(login)
        all <- getRequests
        res <- all.as[DefaultResult[List[VotedFeature]]]
        _ <- deleteUser(u.username)
      } yield {
        res.result.exists(_.feature.title == feat.title)
      }

      test.unsafeRunSync()
    }
  }
}
