package h4sm.files

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, Timer}
import cats.implicits._
import h4sm.auth.infrastructure.endpoint.AuthEndpoints
import h4sm.db.config.DatabaseConfig
import h4sm.files.config._
import h4sm.files.infrastructure.endpoint.FileEndpoints
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import tsec.passwordhashers.jca.BCrypt
import infrastructure.backends._
import org.http4s.server.Router

import scala.concurrent.ExecutionContext

class Server[
  F[_] : ConcurrentEffect
       : ConfigAsk
       : ContextShift
       : Timer
       : ServerConfigAsk
       : DBConfigAsk] {

  val DBC = implicitly[DBConfigAsk[F]]
  val S = implicitly[ServerConfigAsk[F]]
  val C = implicitly[ConfigAsk[F]]
  val E = implicitly[ConcurrentEffect[F]]

  def app(xa: Transactor[F], serverConf : ServerConfig, ec : ExecutionContext): F[ExitCode] = {
    implicit val e = ec
    val authEndpoints = AuthEndpoints.persistingEndpoints(xa, BCrypt)
    implicit val fileMetaStorage = new FileMetaService (xa) (E)
    implicit val fileStorage = new LocalFileStoreService[F]
    val fileEndpoints = new FileEndpoints[F] (authEndpoints)

    val httpApp = Router(
      "/auth" -> authEndpoints.endpoints,
      "/files" -> fileEndpoints.endpoints
    ).orNotFound

    BlazeServerBuilder[F]
    .bindHttp(serverConf.port, serverConf.host)
    .withHttpApp(httpApp)
    .serve
    .compile
    .drain
    .as(ExitCode.Success)
  }
///  F : Bracket[F, Throwable]
  def run(implicit ec : ExecutionContext): F[ExitCode] = {
    for {
      db <- DBC.ask
      serverConf <- S.ask
      _ <- C.ask
      _ <- E.delay(DatabaseConfig.initialize(db)("ct_auth", "ct_files"))
      exitCode <- HikariTransactor
                    .newHikariTransactor(db.driver, db.url, db.user, db.password, ec, ec)
                    .use(app(_, serverConf, ec))
    } yield exitCode
  }
}
