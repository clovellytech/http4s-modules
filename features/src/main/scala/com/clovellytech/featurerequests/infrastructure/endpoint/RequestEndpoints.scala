package com.clovellytech.featurerequests.infrastructure.endpoint


import cats.data.Kleisli
import cats.effect.Effect
import com.clovellytech.featurerequests.domain.requests.{Feature, RequestService}
import org.http4s.{EntityDecoder, HttpService}
import org.http4s.dsl.Http4sDsl

class RequestEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  implicit val featureDecoder : EntityDecoder[F, Feature]

  def endpoints(requestService: RequestService[F]) : HttpService[F] = HttpService {
    case req @ POST -> Root / "vote" => for {
      feature <- req.as[Feature]
      _ <- requestService.makeRequest(feature)
      result <- Ok()
    } yield ()
  }
}
