package h4sm.featurerequests

import cats.effect._
import cats.implicits._
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import h4sm.auth.infrastructure.endpoint.AuthEndpoints
import h4sm.db.config._
import h4sm.featurerequests.config.FeatureRequestConfig
import io.circe.generic.auto._
import infrastructure.endpoint._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

import scala.concurrent.ExecutionContext
import tsec.passwordhashers.jca.BCrypt

class Server[F[_] : ConcurrentEffect : Timer : ContextShift] {
  val E = implicitly[ConcurrentEffect[F]]

  def app(xa : Transactor[F], port : Int, host : String) : F[ExitCode] = {
    val authEndpoints = AuthEndpoints.persistingEndpoints(xa, BCrypt)
    val authService = authEndpoints.Auth
    val requestEndpoints = RequestEndpoints.persistingEndpoints(xa)
    val voteEndpoints = VoteEndpoints.persistingEndpoints(xa)
    val requestApp = requestEndpoints.unAuthEndpoints <+> authService.liftService(requestEndpoints.authEndpoints)
    val httpApp = Router(
      "/auth" -> authEndpoints.endpoints,
      "/" -> requestApp,
      "/vote" -> authService.liftService(voteEndpoints.endpoints)
    ).orNotFound
    BlazeServerBuilder[F]
      .bindHttp(port, host)
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }

  def run(ec : ExecutionContext) : F[ExitCode] = for {
    cfg <- loadConfigF[F, FeatureRequestConfig]()
    FeatureRequestConfig(host, port, db) = cfg
    _ <- E.delay(DatabaseConfig.initialize(db)("ct_auth", "featurerequests"))
    exitCode <- HikariTransactor
                  .newHikariTransactor(db.driver, db.url, db.user, db.password, ec, ec)
                  .use(app(_, port, host))
  } yield exitCode
}
