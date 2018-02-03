package com.clovellytech.featurerequests

import cats.effect._
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze.BlazeBuilder

import db.config._
import config._
import domain.votes.VoteService
import domain.requests.RequestService

import infrastructure.repository.persistent.{RequestRepositoryInterpreter, VoteRepositoryInterpreter}
import infrastructure.endpoint._

object Server extends StreamApp[IO] {

  override def stream(args: List[String], shutdown: IO[Unit]): Stream[IO, ExitCode] =
    createStream[IO](args, shutdown)

  def createStream[F[_] : Effect](args: List[String], shutdown: F[Unit]): Stream[F, ExitCode] = for {
    conf             <- Stream.eval(loadConfig[F, FeatureRequestConfig]("featurerequests"))
    xa               <- Stream.eval(conf.db.dbTransactor[F])
    _                <- Stream.eval(DatabaseConfig.initializeDb[F](xa))
    voteRepo         =  new VoteRepositoryInterpreter[F](xa)
    requestRepo      =  new RequestRepositoryInterpreter[F](xa)
    voteService      =  new VoteService[F](voteRepo)
    requestService   =  new RequestService[F](requestRepo)
    voteEndpoints    = new VoteEndpoints[F].endpoints(voteService)
    requestEndpoints = new RequestEndpoints[F].endpoints(requestService)
    exitCode         <- BlazeBuilder[F]
      .bindHttp(8080, "localhost")
      .mountService(requestEndpoints, "/")
      .mountService(voteEndpoints, "/")
      .serve
  } yield exitCode
}
