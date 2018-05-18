package votingapp

import cats.effect._
import cats.implicits._
import com.clovellytech.auth.infrastructure.endpoint.AuthEndpoints
import com.clovellytech.db.config._
import com.clovellytech.featurerequests.infrastructure.endpoint.{RequestEndpoints, VoteEndpoints}
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze.BlazeBuilder
import tsec.passwordhashers.jca.BCrypt

import scala.concurrent.ExecutionContext

class Server[F[_] : Effect] extends StreamApp[F] {

  override def stream(args: List[String], shutdown: F[Unit]): Stream[F, ExitCode] =
    createStream(args, shutdown)(ExecutionContext.global)

  def createStream(args: List[String], shutdown: F[Unit])(
    implicit ec : ExecutionContext
  ): Stream[F, ExitCode] = for {
      conf <- Stream.eval(loadConfig[F, DatabaseConfig]("db"))
      xa <- Stream.eval(conf.dbTransactor[F])
      _ <- Stream.eval(com.clovellytech.featurerequests.db.initializeAll(xa.kernel))
      authEndpoints = AuthEndpoints.persistingEndpoints(xa, BCrypt)
      authService =  authEndpoints.Auth
      requestEndpoints =  RequestEndpoints.persistingEndpoints(xa)
      voteEndpoints =  VoteEndpoints.persistingEndpoints(xa)
      exitCode <- BlazeBuilder[F]
        .bindHttp(8080, "localhost")
        .mountService(authEndpoints.endpoints, "/auth/")
        .mountService(requestEndpoints.unAuthEndpoints <+> authService.liftService(requestEndpoints.authEndpoints), "/requests")
        .mountService(authService.liftService(voteEndpoints.endpoints), "/vote")
        .serve
    } yield exitCode
}

object IOServer extends Server[IO]
