package h4sm.featurerequests
package infrastructure.endpoint

import cats.effect.Sync
import cats.implicits._
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import tsec.authentication._
import domain.requests._
import h4sm.auth._
import h4sm.featurerequests.infrastructure.repository.persistent.RequestRepositoryInterpreter
import h4sm.featurerequests.db.domain.Feature
import doobie.util.transactor.Transactor

class RequestEndpoints[F[_]: Sync : RequestRepositoryAlgebra] extends Http4sDsl[F] {
  val requestService = implicitly[RequestRepositoryAlgebra[F]]

  def unAuthEndpoints : HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "request" => for {
      feats <- requestService.selectWithVoteCounts
      resp <- Ok(DefaultResult(feats).asJson)
    } yield resp
  }

  def authEndpoints: BearerAuthService[F] = BearerAuthService {
    case req @ POST -> Root / "request" asAuthed _ => for {
      featureRequest <- req.request.as[FeatureRequest]
      feature = Feature(req.authenticator.identity.some, featureRequest.title, featureRequest.description)
      _ <- requestService.insert(feature)
      resp <- Ok()
    } yield resp
  }
}

object RequestEndpoints {
  def persistingEndpoints[F[_] : Sync](xa: Transactor[F]) : RequestEndpoints[F] = {
    implicit val requestRepo = new RequestRepositoryInterpreter(xa)
    new RequestEndpoints[F]
  }
}
