package com.clovellytech.featurerequests
package infrastructure.endpoint

import org.http4s._
import org.http4s.dsl.io._
import org.http4s.client.dsl.io._

import cats.effect.IO

import config.FeatureRequestConfig
import domain.requests.{FeatureRequest, RequestService}
import domain.votes.VoteService

import db.config.loadConfig

import infrastructure.repository.persistent.{RequestRepositoryInterpreter, VoteRepositoryInterpreter}

object helpers {

  val requestEndpoints: IO[HttpService[IO]] = for {
    cfg <- loadConfig[IO, FeatureRequestConfig]("featurerequests")
    xa <- db.getTransactor(cfg.db)
  } yield {
    val interp = new RequestRepositoryInterpreter[IO](xa)
    val service = new RequestService[IO](interp)
    new RequestEndpoints[IO].endpoints(service)
  }

  val voteEndpoints: IO[HttpService[IO]] = for {
    cfg <- loadConfig[IO, FeatureRequestConfig]("featurerequests")
    xa <- db.getTransactor(cfg.db)
  } yield {
    val interp = new VoteRepositoryInterpreter[IO](xa)
    val service = new VoteService[IO](interp)
    new VoteEndpoints[IO].endpoints(service)
  }

  def addRequest(req: FeatureRequest): IO[Option[Response[IO]]] = for {
    eps <- requestEndpoints
    req <- POST(uri("/request"), req)
    res <- eps.run(req).value
  } yield res


  def getRequests(): IO[Option[Response[IO]]] = for {
    eps <- requestEndpoints
    req <- GET(uri("/requests"))
    res <- eps.run(req).value
  } yield res
}