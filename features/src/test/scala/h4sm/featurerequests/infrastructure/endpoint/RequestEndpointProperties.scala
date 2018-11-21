package h4sm.featurerequests
package infrastructure.endpoint

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import cats.effect.IO
import h4sm.auth.infrastructure.endpoint.UserRequest
import db.sql.testTransactor.testTransactor
import domain.requests._
import arbitraries._

object RequestEndpointProperties extends Properties("RequestEndpoint") {
  val eps = new TestRequests[IO](testTransactor)
  import eps._
  import authTestEndpoints._

  property("request can be stored") = forAll { (feat: FeatureRequest, u: UserRequest) =>
    val test : IO[Boolean] = for {
      _ <- postUser(u)
      login <- loginUser(u)
      _ <- addRequest(feat)(login)
      all <- getRequests
      res <- all.as[DefaultResult[List[VotedFeatures]]]
      _ <- deleteUser(u.username)
    } yield {
      res.result.exists(_.feature.title == feat.title)
    }

    test.unsafeRunSync()
  }
}
