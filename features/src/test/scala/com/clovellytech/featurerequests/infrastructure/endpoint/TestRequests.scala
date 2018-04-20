package com.clovellytech.featurerequests
package infrastructure.endpoint

import cats.data.OptionT
import cats.effect.Sync
import cats.implicits._
import com.clovellytech.auth.testing.AuthTestEndpoints
import org.http4s._
import org.http4s.dsl._
import org.http4s.client.dsl._
import domain.requests.{FeatureRequest, RequestService}
import domain.votes.VoteService
import doobie.util.transactor.Transactor
import infrastructure.repository.persistent.{RequestRepositoryInterpreter, VoteRepositoryInterpreter}

class TestRequests[F[_]: Sync](xa: Transactor[F]) extends Http4sDsl[F] with Http4sClientDsl[F] {

  val authTestEndpoints = new AuthTestEndpoints(xa)

  val Auth = authTestEndpoints.authEndpoints.Auth

  val requestEndpoints: HttpService[F] = {
    val interp = new RequestRepositoryInterpreter[F](xa)
    val service = new RequestService[F](interp)
    new RequestEndpoints[F](service).endpoints
  }

  val voteEndpoints: HttpService[F] = {
    val interp = new VoteRepositoryInterpreter[F](xa)
    val service = new VoteService[F](interp)
    new VoteEndpoints[F](service).endpoints
  }

  def addRequest(req: FeatureRequest) : F[OptionT[F, Response[F]]] =
    POST(uri("/request"), req).map(requestEndpoints run _)

  def getRequests: F[OptionT[F, Response[F]]] = GET(uri("/request")).map(requestEndpoints run _)
}
