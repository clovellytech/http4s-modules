package votingapp

import cats.effect._
import cats.implicits._
import doobie.hikari.HikariTransactor
import h4sm.auth.infrastructure.endpoint.AuthEndpoints
import h4sm.db.config._
import h4sm.featurerequests.infrastructure.endpoint.{RequestEndpoints, VoteEndpoints}
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

class Server[F[_] : Effect] extends StreamApp[F] {

  override def stream(args: List[String], shutdown: F[Unit]): Stream[F, ExitCode] =
    createStream(ExecutionContext.global)

  def createStream(ec : ExecutionContext): Stream[F, ExitCode] = {
    val conf = pureconfig.loadConfigOrThrow[DatabaseConfig]("db")
    for {
      xa <- Stream.eval(HikariTransactor.newHikariTransactor(conf.driver, conf.url, conf.user, conf.password))

      // Initializes the database schema for both the features module and auth module as a dependency.
      _ <- Stream.eval(h4sm.featurerequests.db.initializeAll(xa.kernel))

      // Auth module provides default instances needed to create endpoints.
      authEndpoints = AuthEndpoints.persistingEndpoints(xa)
      authService = authEndpoints.Auth
      authHttpService = authEndpoints.endpoints

      requestEndpoints = RequestEndpoints.persistingEndpoints(xa)
      requestHttpService = requestEndpoints.unAuthEndpoints <+> authService.liftService(requestEndpoints.authEndpoints)

      voteEndpoints = VoteEndpoints.persistingEndpoints(xa)
      voteHttpService = authService.liftService(voteEndpoints.endpoints)

      exitCode <- BlazeBuilder[F]
        .bindHttp(8080, "localhost")
        .mountService(authHttpService, "/auth")
        .mountService(requestHttpService, "/requests")
        .mountService(voteHttpService, "/vote")
        .serve
    } yield exitCode
  }
}

object IOServer extends Server[IO]
