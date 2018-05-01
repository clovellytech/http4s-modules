package com.clovellytech.featurerequests
package infrastructure.endpoint

import cats.effect.Sync
import cats.implicits._
import io.circe.syntax._
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import tsec.authentication._
import com.clovellytech.featurerequests.db.domain.{Feature, FeatureId}
import domain.requests._
import com.clovellytech.auth._
import com.clovellytech.featurerequests.infrastructure.repository.persistent.RequestRepositoryInterpreter
import doobie.util.transactor.Transactor

final case class VotedFeatures(featureId: FeatureId, feature: Feature, dateCreated: Instant, upvotes: Long, downvotes: Long)

class RequestEndpoints[F[_]: Sync](requestService: RequestService[F]) extends Http4sDsl[F] {

  def unAuthEndpoints : HttpService[F] = HttpService {
    case GET -> Root / "request" => for {
      res <- requestService.listAll
      feats = res.map(VotedFeatures.tupled)
      resp <- Ok(DefaultResult(feats).asJson)
    } yield resp
  }

  def authEndpoints: BearerAuthService[F] = BearerAuthService {
    case req @ POST -> Root / "request" asAuthed _ => for {
      featureRequest <- req.request.as[FeatureRequest]
      feature = Feature(req.authenticator.identity.some, featureRequest.title, featureRequest.description)
      _ <- requestService.makeRequest(feature)
      resp <- Ok()
    } yield resp
  }
}

object RequestEndpoints {
  def persistingEndpoints[F[_] : Sync](xa: Transactor[F]) : RequestEndpoints[F] = {
    val requestRepo = new RequestRepositoryInterpreter(xa)
    val requestService = new RequestService(requestRepo)
    new RequestEndpoints(requestService)
  }
}
