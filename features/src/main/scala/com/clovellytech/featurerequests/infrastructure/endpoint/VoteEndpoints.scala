package com.clovellytech.featurerequests
package infrastructure.endpoint

import cats.effect.Effect
import cats.implicits._
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl
import domain.votes.{VoteRequest, VoteService}

class VoteEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  def endpoints(voteService: VoteService[F]) : HttpService[F] = HttpService {
    case req @ POST -> Root / "vote" => for {
      vote <- req.as[VoteRequest]
      resp <- Ok()
    } yield resp
  }
}
