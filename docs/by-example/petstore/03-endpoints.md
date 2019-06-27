---
id:      endpoints
title:   "Http4s Modules by Example - Build Endpoints"
---

Now that our algebras are complete for storing and querying pets and orders, let us work through adding endpoints for interacting with these data.

Eventually we will need to handle permissions, to allow only employees of the petstore to add pets and update ship dates on orders. For now let's implement the endpoints only requiring registered users.

```scala mdoc
import cats.effect.Sync
import cats.implicits._
import h4sm.auth._
import h4sm.auth.domain._
import h4sm.auth.domain.tokens._
import h4sm.auth.domain.tokens.AsBaseToken.ops._
import h4sm.petstore.domain._
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl
import tsec.authentication._


final case class PetRequest(name: String, bio: Option[String], status: String)
final case class SiteResult[A](result: A)

class Codecs[F[_]: Sync]{
  implicit val prDecoder: EntityDecoder[F, PetRequest] = jsonOf
  implicit def siteResEnc[A: Encoder]: EntityEncoder[F, SiteResult[A]] = jsonEncoderOf
  implicit val petEnc: EntityEncoder[F, PetRequest] = jsonEncoderOf
  implicit def siteResultDec[A: Decoder]: EntityDecoder[F, SiteResult[A]] = jsonOf
}

class PetEndpoints[F[_]: Sync: PetAlgebra, T[_]](auth : UserSecuredRequestHandler[F, T])(implicit
  B: AsBaseToken[T[UserId]]
) extends Http4sDsl[F]{

  val codecs = new Codecs[F]
  import codecs._

  def addPet: UserAuthService[F, T] = UserAuthService {
    case req@POST -> Root asAuthed _ => for {
      pr <- req.request.as[PetRequest]
      createdBy = req.authenticator.asBase.identity
      _ <- PetAlgebra[F].insert(Pet(pr.name, pr.bio, createdBy, pr.status))
      res <- Ok()
    } yield res
  }

  def listPets: UserAuthService[F, T] = UserAuthService {
    case _@GET -> Root asAuthed _ => for {
      ps <- PetAlgebra[F].select
      res <- Ok(SiteResult(ps))
    } yield res
  }
 
  def authService = addPet  // this and other endpoints would be joined with combineK, or <+> here.

  def endpoints = auth.liftService(authService)
}
```
*/petstore/infrastructure/endpoints/PetEndpoints.scala*

Note we're using PetAlgebra as a typeclass. This means when it comes time to create these endpoints, we'll need an implicit instance of PetAlgebra[F] available for whatever F gets chosen to run our server with. You'll see how that comes together in the next section. See `petstore/domain/PetAlgebra.scala`. 

This and other endpoints have been defined in `petstore/infrastructure/endpoint/PetEndpoints`, and the endpoints for orders should be reviewed as well.

### About those endpoints...
See the `auth` argument, and that weird `T[_]` type parameter? This allows the user to specify what token type they are using for our authentication. This is what allows these pet endpoints to be used with any kind of authentication we may choose later. `AsBaseToken[T[UserId]]` is stated as a requirement that whatever `T` we choose later, the T must allow us to view the Token as a BaseToken that provides a UserId directly after authenticating the user.

So let's create our endpoints and throw some requests at them. First we'll need to authenticate ourselves, so we'll also need to create some auth endpoints. So we'll start by building up a stack of repositories required for all these endpoints to get built:


```scala mdoc
import cats.effect.{Resource, ConcurrentEffect, ContextShift, Timer, Sync}
import doobie._
import doobie.hikari.HikariTransactor
import h4sm.auth.domain.tokens._
import h4sm.auth.domain.users._
import h4sm.auth.infrastructure.endpoint._
import h4sm.auth.infrastructure.repository.persistent._
import h4sm.auth.client._
import h4sm.db.config._
import h4sm.petstore.domain._
import h4sm.petstore.infrastructure.repository.persistent._
import io.circe.config.parser
import tsec.passwordhashers.jca.BCrypt

def petEndpoints[
  F[_]: Sync
  : ConcurrentEffect
  : Timer
  : ContextShift
]: Resource[F, (AuthClient[F, BCrypt, TSecBearerToken], PetEndpoints[F, TSecBearerToken])] = for {
  // load our configuration and get a transactor for doobie:
  db <- Resource.liftF(parser.decodePathF[F, DatabaseConfig]("db"))
  // Initializes our entire database, including our authentication dependency:
  _ <- Resource.liftF(DatabaseConfig.initialize[F](db)("ct_auth", "ct_petstore"))
  connec <- ExecutionContexts.fixedThreadPool[F](4)
  tranec <- ExecutionContexts.cachedThreadPool[F]
  xa <- HikariTransactor.newHikariTransactor[F](db.driver, db.url, db.user, db.password, connec, tranec)

  // These 5 lines are the core of the approach of this project. Build up the interpreters
  // and other requirements of our endpoints, so that we can easily instantiate them.
  implicit0(us: UserRepositoryAlgebra[F]) = new UserRepositoryInterpreter(xa)
  implicit0(ts: TokenRepositoryAlgebra[F]) = new TokenRepositoryInterpreter(xa)
  implicit0(ps: PetAlgebra[F]) = new PetRepository(xa)
  userService = new UserService[F, BCrypt](BCrypt)
  authenticator = Authenticators.bearer
  auth = SecuredRequestHandler(authenticator)

} yield (new AuthClient(userService, authenticator), new PetEndpoints(auth))
```

At this point we will switch into `IO`:

```scala mdoc
import cats.effect.IO
import h4sm.auth.infrastructure.endpoint._
import h4sm.petstore.infrastructure.endpoint._
import h4sm.petstore.domain._
import io.circe.generic.auto._
import io.circe.java8.time._
import org.http4s.dsl.io._
import org.http4s.client.dsl.io._
import org.http4s.circe._
import org.http4s.Uri.uri
import scala.concurrent.ExecutionContext.Implicits.global

// see petstore.ServerMain for a complete example of creating a server. 
implicit val cs = IO.contextShift(global)
implicit val timer = IO.timer(global)

val codecs = new Codecs[IO]
import codecs._

implicit val lstDec: EntityDecoder[IO, List[(Pet, PetId, Instant)]] = jsonOf

val ur = UserRequest("demo", "demopassword")

val insertAndRetrieve: Resource[IO, List[(Pet, PetId, Instant)]] = for {
  (auth, pets) <- petEndpoints[IO]
  _ <- Resource.liftF(auth.postUser(ur))
  login <- Resource.liftF(auth.loginUser(ur))
  inject = auth.injectAuthHeader(login) _
  createPetReq <- Resource.liftF(POST(PetRequest("Sparky", Some("A great dog"), "available"), uri("/")))
  _ <- Resource.liftF(pets.endpoints.orNotFound.run(inject(createPetReq)))
  listAllReq <- Resource.liftF(GET(uri("/")))
  allResp <- Resource.liftF(pets.endpoints.orNotFound.run(inject(listAllReq)))
  petsResult <- Resource.liftF(allResp.as[SiteResult[List[(Pet, PetId, Instant)]]])
} yield petsResult.result

```

[Now let's write a Server class that will hook these endpoints up.](04-server.md)
