package com.clovellytech.featurerequests
package infrastructure.endpoint

import cats.effect.{Async, Effect}
import cats.implicits._
import org.http4s._
import org.http4s.dsl._
import org.http4s.client.dsl._
import domain.requests.{FeatureRequest, RequestService}
import domain.votes.VoteService
import infrastructure.repository.persistent.{RequestRepositoryInterpreter, VoteRepositoryInterpreter}

class TestRequests[F[_]: Effect : Async] extends Http4sDsl[F] with Http4sClientDsl[F] {

  val requestEndpoints: F[HttpService[F]] = db.getTransactor[F].map{ xa =>
    val interp = new RequestRepositoryInterpreter[F](xa)
    val service = new RequestService[F](interp)
    new RequestEndpoints[F].endpoints(service)
  }

  val voteEndpoints: F[HttpService[F]] = db.getTransactor[F].map{ xa =>
    val interp = new VoteRepositoryInterpreter[F](xa)
    val service = new VoteService[F](interp)
    new VoteEndpoints[F].endpoints(service)
  }

  def addRequest(req: FeatureRequest) : F[Option[Response[F]]] = for {
    eps <- requestEndpoints
    req <- POST(uri("/request"), req)
    res <- eps.run(req).value
  } yield res


  def getRequests(): F[Option[Response[F]]] = for {
    eps <- requestEndpoints
    req <- GET(uri("/requests"))
    res <- eps.run(req).value
  } yield res
}
