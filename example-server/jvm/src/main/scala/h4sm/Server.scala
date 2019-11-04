package h4sm

import cats.effect._
import cats.implicits._
import auth.infrastructure.endpoint.{AuthEndpoints, Authenticators}
import auth.infrastructure.repository.persistent.{TokenRepositoryInterpreter, UserRepositoryInterpreter}
import auth.domain._
import auth.domain.tokens._
import auth.domain.users.UserRepositoryAlgebra
import db.config._
import doobie._
import doobie.hikari.HikariTransactor
import files.infrastructure.endpoint.FileEndpoints
import org.http4s.HttpRoutes
import org.http4s.server.{Router, Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import scala.concurrent.ExecutionContext
import tsec.passwordhashers.jca._

/*
 * Build a server that uses every module in this project...
 */
class H4SMServer[F[_]: ContextShift: ConcurrentEffect: Timer: files.config.ConfigAsk](implicit
  C: ConfigAsk[F]
) {

  def router[A, T[_]](testMode: Boolean, auth: AuthEndpoints[F, A, T], files: FileEndpoints[F, T]): HttpRoutes[F] = {
    Router(
      "/users" -> {
        if (testMode) auth.testService <+> auth.unauthService <+> auth.Auth.liftService(auth.authService)
        else auth.endpoints
      },
      "/files" -> files.endpoints
    )
  }

  def createServer: Resource[F, Server[F]] = {
    for {
      cfg <- Resource.liftF(C.ask)
      _ = println("Config:")
      _ = println(cfg)
      MainConfig(db, fc, ServerConfig(host, port, numThreads), test, allowCors, logging) = cfg
      _ = if (test) println("DANGER, RUNNING IN TEST MODE!!")
      connec <- ExecutionContexts.fixedThreadPool[F](numThreads)
      tranec <- ExecutionContexts.cachedThreadPool[F]
      xa <- HikariTransactor.newHikariTransactor[F](db.driver, db.url, db.user, db.password, connec, Blocker.liftExecutionContext(tranec))
      implicit0(us: UserRepositoryAlgebra[F]) = new UserRepositoryInterpreter(xa)
      userService = new UserService[F, BCrypt](BCrypt)
      implicit0(ts: TokenRepositoryAlgebra[F]) = new TokenRepositoryInterpreter(xa)
      authEndpoints = new AuthEndpoints(userService, Authenticators.bearer)
      _ <- Resource.liftF(DatabaseConfig.initialize[F](db)("ct_auth", "ct_files"))
      files = FileEndpoints.persistingEndpoints(xa, authEndpoints.Auth, Blocker.liftExecutionContext(ExecutionContext.Implicits.global))
      service = router(test, authEndpoints, files).orNotFound
      withCors = if(allowCors){
        import org.http4s.server.middleware.{CORS, CORSConfig}
        import scala.concurrent.duration._
  
        val methodConfig = CORSConfig(
          anyOrigin = true,
          anyMethod = true,
          allowCredentials = true,
          maxAge = 1.day.toSeconds,
        )
        CORS(service, methodConfig)
      } else {
        service
      }
      withMiddleware = if(logging) {        
        import org.http4s.server.middleware.Logger

        Logger.httpApp(true, true, redactHeadersWhen = _ => false)(withCors)
      } else {
        withCors
      }
      server <- BlazeServerBuilder[F]
                .bindHttp(port, host)
                .withHttpApp(withMiddleware)
                .resource
    } yield server
  }
}

object ServerMain extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    implicit val cfg: ConfigAsk[IO] = getConfigAsk[IO].map { (c: MainConfig) =>
      args match {
        case "test" :: _ => c.copy(test = true)
        case _ => c
      }
    }

    implicit val fcfg: files.config.ConfigAsk[IO] = getFileConfigAsk

    val server = new H4SMServer[IO]

    server.createServer.use(_ => IO.never).as(ExitCode.Success)
  }
}
