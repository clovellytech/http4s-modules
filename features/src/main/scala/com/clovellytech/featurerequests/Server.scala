package com.clovellytech.featurerequests

import scala.concurrent.ExecutionContext
import cats.effect._
import cats.implicits._
import com.clovellytech.auth.infrastructure.endpoint.AuthEndpoints
import com.clovellytech.db.config.DatabaseConfig
import com.clovellytech.featurerequests.config.FeatureRequestConfig
import doobie.hikari.HikariTransactor
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze.BlazeBuilder
import infrastructure.endpoint._
import tsec.passwordhashers.jca.BCrypt

class Server[F[_] : Effect] extends StreamApp[F] {
  override def stream(args: List[String], shutdown: F[Unit]): Stream[F, ExitCode] =
    createStream(shutdown)(ExecutionContext.Implicits.global)

  def createStream(shutdown: F[Unit])(implicit ec : ExecutionContext): Stream[F, ExitCode] = {
    val FeatureRequestConfig(host, port, db) = pureconfig.loadConfigOrThrow[FeatureRequestConfig]
    for {
      xa <- Stream.eval(HikariTransactor.newHikariTransactor[F](db.driver, db.url, db.user, db.password))
      _ <- Stream.eval(DatabaseConfig.initializeFromTransactor(xa)("featurerequests"))
      authEndpoints = AuthEndpoints.persistingEndpoints(xa, BCrypt)
      authService = authEndpoints.Auth
      requestEndpoints = RequestEndpoints.persistingEndpoints(xa)
      voteEndpoints = VoteEndpoints.persistingEndpoints(xa)
      exitCode <- BlazeBuilder[F]
        .bindHttp(port, host)
        .mountService(authEndpoints.endpoints, "/auth/")
        .mountService(requestEndpoints.unAuthEndpoints <+> authService.liftService(requestEndpoints.authEndpoints), "/")
        .mountService(authService.liftService(voteEndpoints.endpoints), "/")
        .serve
      _ <- Stream.eval(shutdown)
    } yield exitCode
  }
}
