package h4sm.featurerequests
package infrastructure.endpoint

import cats.effect.{Sync, Bracket}
import cats.implicits._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import tsec.authentication._
import domain.requests._
import h4sm.auth._
import h4sm.featurerequests.infrastructure.repository.persistent.RequestRepositoryInterpreter
import h4sm.featurerequests.db.domain.Feature
import doobie.util.transactor.Transactor
import h4sm.auth.comm.SiteResult
import h4sm.auth.comm.codecs._
import org.http4s.circe.CirceEntityCodec._

class RequestEndpoints[F[_]: Sync: RequestRepositoryAlgebra] extends Http4sDsl[F] {
  def unAuthEndpoints: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "request" => for {
      feats <- RequestRepositoryAlgebra[F].selectWithVoteCounts
      resp <- Ok(SiteResult(feats))
    } yield resp
  }

  def authEndpoints: BearerAuthService[F] = BearerAuthService {
    case req @ POST -> Root / "request" asAuthed _ => for {
      featureRequest <- req.request.as[FeatureRequest]
      feature = Feature(req.authenticator.identity.some, featureRequest.title, featureRequest.description)
      _ <- RequestRepositoryAlgebra[F].insert(feature)
      resp <- Ok()
    } yield resp
  }
}

object RequestEndpoints {
  def persistingEndpoints[F[_]: Sync: Bracket[?[_], Throwable]](xa: Transactor[F]): RequestEndpoints[F] = {
    implicit val requestRepo = new RequestRepositoryInterpreter(xa)
    new RequestEndpoints[F]
  }
}
