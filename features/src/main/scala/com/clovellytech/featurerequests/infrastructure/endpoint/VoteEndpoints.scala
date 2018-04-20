package com.clovellytech.featurerequests
package infrastructure
package endpoint

import cats.effect.Sync
import cats.implicits._
import org.http4s.dsl.Http4sDsl
import domain.votes.{VoteRequest, VoteService}
import org.http4s.HttpService

class VoteEndpoints[F[_]: Sync](voteService: VoteService[F]) extends Http4sDsl[F] {

  def endpoints : HttpService[F] = HttpService {
    case req @ POST -> Root / "vote" => for {
      vote <- req.as[VoteRequest]
      resp <- Ok()
    } yield resp
  }
}
