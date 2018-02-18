package com.clovellytech.featurerequests
package infrastructure.endpoint


import java.util.UUID

import cats.effect.Effect
import cats.implicits._
import com.clovellytech.featurerequests.db.domain.{Feature, FeatureId}
import domain.requests.{FeatureRequest, RequestService}
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.joda.time.DateTime

final case class VotedFeatures(featureId: FeatureId, feature: Feature, dateCreated: DateTime, upvotes: Long, downvotes: Long)

class RequestEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  def endpoints(requestService: RequestService[F]) : HttpService[F] = HttpService {
    case req @ POST -> Root / "request" => for {
      featureRequest <- req.as[FeatureRequest]
      feature : Feature = Feature(UUID.randomUUID().some, featureRequest.title, featureRequest.description)
      _ <- requestService.makeRequest(feature)
      result <- Ok()
    } yield result

    case GET -> Root / "request" => for {
      res <- requestService.listAll
      feats = res.map(VotedFeatures.tupled)
      resp <- Ok(DefaultResult(feats))
    } yield resp
  }
}
