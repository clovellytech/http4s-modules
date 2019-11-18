package h4sm.featurerequests
package infrastructure.endpoint

import cats.effect.IO
import h4sm.auth.comm.UserRequest
import h4sm.testutil.DbFixtureSuite
import h4sm.auth.comm.arbitraries._
import h4sm.auth.comm.SiteResult
import h4sm.auth.comm.codecs._
import h4sm.featurerequests.comm.arbitraries._
import h4sm.featurerequests.comm.domain.votes._
import h4sm.featurerequests.comm.domain.features._
import h4sm.featurerequests.comm.codecs._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.http4s.circe.CirceEntityCodec._

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
        res <- all.as[SiteResult[List[VotedFeature]]]
        _ <- deleteUser(u.username)
      } yield {
        res.result.exists(_.feature.title == feat.title)
      }

      test.unsafeRunSync()
    }
  }
}
