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

object Server extends StreamApp[IO] {

  override def stream(args: List[String], shutdown: IO[Unit]): Stream[IO, ExitCode] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    createStream[IO](args, shutdown)
  }

  def createStream[F[_] : Effect](args: List[String], shutdown: F[Unit])(
    implicit ec : ExecutionContext
  ): Stream[F, ExitCode] = for {
      conf <- Stream.eval(loadConfig[F, DatabaseConfig]("db"))
      xa <- Stream.eval(conf.dbTransactor[F])
      _ <- Stream.eval(com.clovellytech.featurerequests.db.initializeAll(xa))
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
