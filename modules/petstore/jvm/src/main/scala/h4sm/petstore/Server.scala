package h4sm
package petstore

import cats.effect._
import auth.infrastructure.endpoint.{AuthEndpoints, Authenticators}
import auth.infrastructure.repository.persistent.{
  TokenRepositoryInterpreter,
  UserRepositoryInterpreter,
}
import auth.domain.tokens._
import auth.domain.UserService
import auth.domain.users.UserRepositoryAlgebra
import doobie._
import doobie.hikari.HikariTransactor
import io.circe.config.parser
import io.circe.generic.auto._
import h4sm.db.config._
import org.http4s.HttpRoutes
import org.http4s.server.{Router, Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import petstore.domain._
import petstore.infrastructure.endpoint._
import petstore.infrastructure.repository.persistent._
import tsec.passwordhashers.jca._
import tsec.cipher.symmetric.jca._
import tsec.authentication.SecuredRequestHandler

class PetstoreServer[F[_]: ContextShift: ConcurrentEffect: Timer] {
  def router[A, T[_]](
      auth: AuthEndpoints[F, A, T],
      pets: PetEndpoints[F, T],
      orders: OrderEndpoints[F, T],
  ): HttpRoutes[F] =
    Router(
      "/users" -> auth.endpoints,
      "/pets" -> pets.endpoints,
      "/orders" -> orders.endpoints,
    )

  def createServer: Resource[F, Server[F]] = {
    implicit val encryptor = AES128GCM.genEncryptor[F]
    implicit val gcmstrategy = AES128GCM.defaultIvStrategy[F]
    for {
      cfg <- Resource.liftF(parser.decodeF[F, MainConfig])
      MainConfig(db, ServerConfig(host, port, numThreads)) = cfg
      connec <- ExecutionContexts.fixedThreadPool[F](numThreads)
      tranec <- ExecutionContexts.cachedThreadPool[F]
      xa <- HikariTransactor.newHikariTransactor[F](
        db.driver,
        db.url,
        db.user,
        db.password,
        connec,
        Blocker.liftExecutionContext(tranec),
      )
      key <- Resource.liftF(AES128GCM.generateKey[F])
      implicit0(us: UserRepositoryAlgebra[F]) = new UserRepositoryInterpreter(xa)
      implicit0(ts: TokenRepositoryAlgebra[F]) = new TokenRepositoryInterpreter(xa)
      implicit0(ps: PetAlgebra[F]) = new PetRepository(xa)
      implicit0(os: OrderAlgebra[F]) = new OrderRepository(xa)
      userService = new UserService[F, BCrypt](BCrypt)
      authenticator = Authenticators.statelessCookie(key)
      auth = SecuredRequestHandler(authenticator)
      authEndpoints = new AuthEndpoints(userService, authenticator)
      petEndpoints = new PetEndpoints(auth)
      orderEndpoints = new OrderEndpoints(auth)
      _ <- Resource.liftF(DatabaseConfig.initialize[F](db)("ct_auth", "ct_petstore"))
      server <- BlazeServerBuilder[F]
        .bindHttp(port, host)
        .withHttpApp(router(authEndpoints, petEndpoints, orderEndpoints).orNotFound)
        .resource
    } yield server
  }
}

object ServerMain extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val server = new PetstoreServer[IO]
    server.createServer.use(_ => IO.never).as(ExitCode.Success)
  }
}
