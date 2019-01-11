package h4sm.featurerequests
package infrastructure.endpoint

import cats.effect.IO
import h4sm.auth.infrastructure.endpoint.UserRequest
import db.sql.testTransactor._
import domain.requests._
import arbitraries._
import h4sm.featurerequests.db.domain.VotedFeature
import org.scalatest.prop.PropertyChecks

object RequestEndpointProperties extends PropertyChecks with DbFixtureSuite {
  val dbName = "request_endpoints_test_property_spec"

  test("request properties") { p =>
    import p.reqs._
    import authTestEndpoints._

    forAll { (feat: FeatureRequest, u: UserRequest) =>
      val test : IO[Boolean] = for {
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
