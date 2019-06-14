---
id:      endpoints
title:   "Http4s Modules by Example - Build Endpoints"
---

Now that we have our inital endpoints and database set up, all that remains is to tie them together by creating a server:

```scala mdoc 

import cats.effect._
import cats.implicits._
import h4sm.auth.infrastructure.endpoint._
import h4sm.auth.infrastructure.repository.persistent._
import h4sm.auth.domain.tokens._
import h4sm.auth.domain.users.UserRepositoryAlgebra
import h4sm.db.config._
import h4sm.petstore.domain._
import h4sm.petstore.infrastructure.endpoint._
import h4sm.petstore.infrastructure.repository.persistent._
import doobie._
import doobie.hikari.HikariTransactor
import io.circe.config.parser
import io.circe.generic.auto._
import org.http4s.HttpRoutes
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

  def router[A, T[_]](
    auth: AuthEndpoints[F, A, T], 
    pets: PetEndpoints[F, T], 
    orders: OrderEndpoints[F, T]
  ) : HttpRoutes[F] = {
    Router(
      "/users" -> auth.endpoints,
      "/pets" -> pets.endpoints,
      "/orders" -> orders.endpoints
    )
  }

  def createServer : Resource[F, Server[F]] = {
    implicit val encryptor = AES128GCM.genEncryptor[F]
    implicit val gcmstrategy = AES128GCM.defaultIvStrategy[F]
    for {
      db <- Resource.liftF(parser.decodePathF[F, DatabaseConfig]("db"))
      connec <- ExecutionContexts.fixedThreadPool[F](10)
      tranec <- ExecutionContexts.cachedThreadPool[F]
      xa <- HikariTransactor.newHikariTransactor[F](db.driver, db.url, db.user, db.password, connec, tranec)
      key <- Resource.liftF(AES128GCM.generateKey[F])
      implicit0(us: UserRepositoryAlgebra[F]) = new UserRepositoryInterpreter(xa)
      implicit0(ts: TokenRepositoryAlgebra[F]) = new TokenRepositoryInterpreter(xa)
      implicit0(ps: PetAlgebra[F]) = new PetRepository(xa)
      implicit0(os: OrderAlgebra[F]) = new OrderRepository(xa)
      authenticator = Authenticators.statelessCookie(key)
      auth = SecuredRequestHandler(authenticator)
      authEndpoints = new AuthEndpoints(BCrypt, authenticator)
      petEndpoints = new PetEndpoints(auth)
      orderEndpoints = new OrderEndpoints(auth)
      _ <- Resource.liftF(DatabaseConfig.initialize[F](db)("ct_auth", "ct_petstore"))
      server <- BlazeServerBuilder[F]
                .bindHttp(8080, "localhost")
                .withHttpApp(router(authEndpoints, petEndpoints, orderEndpoints).orNotFound)
                .resource
    } yield server
  }
}
```
