package h4sm

import cats.effect._
import cats.implicits._
import doobie._
import auth.infrastructure.endpoint.AuthEndpoints
import doobie.hikari.HikariTransactor
import h4sm.db.config.DatabaseConfig
import h4sm.files.infrastructure.endpoint.FileEndpoints
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import tsec.passwordhashers.jca.BCrypt

import scala.concurrent.ExecutionContext


/*
 * Build a server that uses every module in this project...
 */
class Server[F[_] : ContextShift : ConcurrentEffect : Timer](implicit
  C : ConfigAsk[F]
) {
  def createServer : Resource[F, ExitCode] = for {
    cfg <- Resource.liftF(C.ask)
    MainConfig(db, fc, ServerConfig(host, port, numThreads)) = cfg
    connec <- ExecutionContexts.fixedThreadPool[F](numThreads)
    tranec <- ExecutionContexts.cachedThreadPool[F]
    xa <- HikariTransactor.newHikariTransactor[F](db.driver, db.url, db.user, db.password, connec, tranec)
    _ <- Resource.liftF(DatabaseConfig.initialize[F](db)("ct_auth", "ct_files"))
    auth = AuthEndpoints.persistingEndpoints[F, BCrypt](xa)
    files = FileEndpoints.persistingEndpoints[F](xa, auth, ExecutionContext.Implicits.global)

    exitCode <- Resource.liftF(
      BlazeServerBuilder[F]
        .bindHttp(port, host)
        .withHttpApp(
          Router(
            "users" -> auth.endpoints,
            "files" -> files.endpoints
          ).orNotFound
        )
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    )
  } yield exitCode
}

object ServerMain extends IOApp {
  val server = new Server[IO]

  def run(args: List[String]): IO[ExitCode] = server.createServer.use(IO.pure)
}
