package h4sm.featurerequests
package infrastructure.endpoint

import arbitraries._
import cats.effect.{ContextShift, IO}
import domain.requests._
import h4sm.auth.infrastructure.endpoint.UserRequest
import h4sm.db.config._
import h4sm.dbtesting.DbFixtureSuite
import h4sm.featurerequests.db.domain.VotedFeature
import io.circe.config.parser
import org.scalatest.prop.PropertyChecks

class RequestEndpointProperties extends PropertyChecks with DbFixtureSuite {
  val dbName = "request_endpoints_test_property_spec"
  implicit def cs : ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)
  def schemaNames = Seq("ct_auth", "ct_feature_requests")
  def config : DatabaseConfig = parser.decodePathF[IO, DatabaseConfig]("db").unsafeRunSync()

  test("request properties") { p =>
    val reqs = new TestRequests[IO](p.transactor)
    import reqs._
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
