---
id:      server
title:   "Http4s Modules by Example - Build Endpoints"
---

Now that we have our inital endpoints and database set up, all that remains is to tie them together by creating a server:

```scala mdoc 

import cats.effect._
import cats.implicits._
import h4sm.auth.infrastructure.endpoint._
import h4sm.auth.infrastructure.repository.persistent._
import h4sm.auth.domain.tokens._
import h4sm.auth.domain.UserService
import h4sm.auth.domain.users.UserRepositoryAlgebra
import h4sm.db.config._
import h4sm.petstore.domain._
import h4sm.petstore.infrastructure.endpoint._
import h4sm.petstore.infrastructure.repository.persistent._
import doobie._
import doobie.hikari.HikariTransactor
import io.circe.config.parser
import org.http4s.server.{Router, Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import tsec.passwordhashers.jca._
import tsec.cipher.symmetric.jca._
import tsec.authentication.SecuredRequestHandler

/*
 * Build a server that uses every module in this project...
 */
class PetstoreServer[F[_] : ContextShift : ConcurrentEffect : Timer] {

  def createServer : Resource[F, Server[F]] = {
    implicit val encryptor = AES128GCM.genEncryptor[F]
    implicit val gcmstrategy = AES128GCM.defaultIvStrategy[F]
    for {
      // Set up requirements for Doobie:
      db <- Resource.liftF(parser.decodePathF[F, DatabaseConfig]("db"))
      connec <- ExecutionContexts.fixedThreadPool[F](10)
      tranec <- ExecutionContexts.cachedThreadPool[F]
      blk = Blocker.liftExecutionContext(tranec)
      xa <- HikariTransactor.newHikariTransactor[F](db.driver, db.url, db.user, db.password, connec, blk)

      // Migrate the database, including all of our dependent schemas!
      _ <- Resource.liftF(DatabaseConfig.initialize[F](db)("ct_auth", "ct_petstore"))
      
      // Repositories for endpoints
      implicit0(us: UserRepositoryAlgebra[F]) = new UserRepositoryInterpreter(xa)
      implicit0(ts: TokenRepositoryAlgebra[F]) = new TokenRepositoryInterpreter(xa)
      implicit0(ps: PetAlgebra[F]) = new PetRepository(xa)
      implicit0(os: OrderAlgebra[F]) = new OrderRepository(xa)
      userService = new UserService[F, BCrypt](BCrypt)
      // Key for stateless cookie authenticator
      key <- Resource.liftF(AES128GCM.generateKey[F])
      authenticator = Authenticators.statelessCookie(key)
      auth = SecuredRequestHandler(authenticator)

      // Endpoints
      authEndpoints = new AuthEndpoints(userService, authenticator)
      petEndpoints = new PetEndpoints(auth)
      orderEndpoints = new OrderEndpoints(auth)
 
      // Connect endpoints to base urls:
      httpApp = Router(
        "/users" -> authEndpoints.endpoints,
        "/pets" -> petEndpoints.endpoints,
        "/orders" -> orderEndpoints.endpoints
      ).orNotFound

      server <- BlazeServerBuilder[F]
                .bindHttp(8080, "localhost")
                .withHttpApp(httpApp)
                .resource
    } yield server
  }
}
```


Now with this server class, we're ready for our main method. Only now are we fully specifying how our server will run, with `cats.effect.IO`:

```scala mdoc
object ServerMain extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {

    val server = new PetstoreServer[IO]

    // Never terminate, until owner of this process decides...
    server.createServer.use(_ => IO.never).as(ExitCode.Success)
  }
}
```
