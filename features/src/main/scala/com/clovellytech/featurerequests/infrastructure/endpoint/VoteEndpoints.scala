package com.clovellytech.featurerequests.infrastructure.endpoint


import cats.data.Kleisli
import cats.effect.Effect
import com.clovellytech.featurerequests.domain.votes.VoteService
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl

class VoteEndpoints[F[_]: Effect] extends Http4sDsl[F] {

  def endpoints(voteService: VoteService[F]) : HttpService[F] = Kleisli {}
}
