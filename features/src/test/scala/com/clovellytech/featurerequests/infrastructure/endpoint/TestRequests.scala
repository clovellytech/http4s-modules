package com.clovellytech.featurerequests
package infrastructure.endpoint

import cats.data.OptionT
import cats.effect.{Async, Effect}
import cats.implicits._
import org.http4s._
import org.http4s.dsl._
import org.http4s.client.dsl._
import domain.requests.{FeatureRequest, RequestService}
import domain.votes.VoteService
import doobie.util.transactor.Transactor
import infrastructure.repository.persistent.{RequestRepositoryInterpreter, VoteRepositoryInterpreter}

class TestRequests[F[_]: Effect : Async](xa: Transactor[F]) extends Http4sDsl[F] with Http4sClientDsl[F] {

  val requestEndpoints: HttpService[F] = {
    val interp = new RequestRepositoryInterpreter[F](xa)
    val service = new RequestService[F](interp)
    new RequestEndpoints[F].endpoints(service)
  }

  val voteEndpoints: HttpService[F] = {
    val interp = new VoteRepositoryInterpreter[F](xa)
    val service = new VoteService[F](interp)
    new VoteEndpoints[F].endpoints(service)
  }

  def addRequest(req: FeatureRequest) : F[OptionT[F, Response[F]]] =
    POST(uri("/request"), req).map(requestEndpoints.run(_))

  def getRequests: F[OptionT[F, Response[F]]] = GET(uri("/request")).map(requestEndpoints.run(_))
}
