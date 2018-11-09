package com.clovellytech.files

import cats.effect.Effect
import cats.implicits._
import com.clovellytech.auth.infrastructure.endpoint.AuthEndpoints
import com.clovellytech.db.config.DatabaseConfig
import com.clovellytech.files.config._
import com.clovellytech.files.infrastructure.endpoint.FileEndpoints
import doobie.hikari.HikariTransactor
import fs2.Stream
import fs2.StreamApp.ExitCode
import org.http4s.server.blaze.BlazeBuilder
import tsec.passwordhashers.jca.BCrypt
import infrastructure.backends._

import scala.concurrent.ExecutionContext

trait Server[F[_]] {

  def createStream(shutdown: F[Unit])(implicit
    ec : ExecutionContext,
    E : Effect[F],
    C : ConfigAsk[F],
    S : ServerConfigAsk[F],
    DBC : DBConfigAsk[F]
  ): Stream[F, ExitCode] = {
    for {
      db <- Stream.eval(DBC.ask)
      serverConf <- Stream.eval(S.ask)
      filesConf <- Stream.eval(C.ask)
      xa <- Stream.eval(HikariTransactor.newHikariTransactor[F](db.driver, db.url, db.user, db.password))
      _ <- Stream.eval(DatabaseConfig.initializeFromTransactor(xa)("ct_auth"))
      _ <- Stream.eval(DatabaseConfig.initializeFromTransactor(xa)("ct_files"))
      authEndpoints = AuthEndpoints.persistingEndpoints(xa, BCrypt)
      fileEndpoints = {
        implicit val fileMetaStorage = new FileMetaService(xa)(E)
        implicit val fileStorage = new LocalFileStoreService[F]
        new FileEndpoints[F](authEndpoints)
      }
      exitCode <- BlazeBuilder[F]
        .bindHttp(serverConf.port, serverConf.host)
        .mountService(authEndpoints.endpoints, "/auth/")
        .mountService(fileEndpoints.endpoints, "/files/")
        .serve
      _ <- Stream.eval(shutdown)
    } yield exitCode
  }
}
