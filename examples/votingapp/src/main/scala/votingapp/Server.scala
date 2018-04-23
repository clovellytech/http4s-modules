package votingapp

import com.clovellytech.featurerequests._

import cats.effect._
import cats.implicits._
import com.clovellytech.db.config._
import com.clovellytech.auth.domain.tokens.TokenService
import com.clovellytech.auth.domain.users.UserService
import com.clovellytech.auth.infrastructure.endpoint.AuthEndpoints
import com.clovellytech.auth.infrastructure.repository.persistent.{TokenRepositoryInterpreter, UserRepositoryInterpreter}
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze.BlazeBuilder
import domain.votes.VoteService
import domain.requests.RequestService
import infrastructure.repository.persistent.{RequestRepositoryInterpreter, VoteRepositoryInterpreter}
import infrastructure.endpoint._
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
      voteRepo = new VoteRepositoryInterpreter(xa)
      requestRepo = new RequestRepositoryInterpreter(xa)
      userRepo = new UserRepositoryInterpreter(xa)
      tokenRepo = new TokenRepositoryInterpreter(xa)
      userService = new UserService(userRepo)
      tokenService = new TokenService(tokenRepo)
      voteService = new VoteService(voteRepo)
      requestService = new RequestService(requestRepo)
      authEndpoints = new AuthEndpoints(userService, tokenService, BCrypt)
      authService = authEndpoints.Auth
      requestEndpoints = new RequestEndpoints(requestService)
      voteEndpoints = new VoteEndpoints(voteService)
      exitCode <- BlazeBuilder[F]
        .bindHttp(8080, "localhost")
        .mountService(authEndpoints.endpoints, "/auth/")
        .mountService(requestEndpoints.unAuthEndpoints <+> authService.liftService(requestEndpoints.authEndpoints), "/requests")
        .mountService(authService.liftService(voteEndpoints.endpoints), "/vote")
        .serve
    } yield exitCode

}
