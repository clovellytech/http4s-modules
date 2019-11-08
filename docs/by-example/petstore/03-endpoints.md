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
import h4sm.auth.comm.SiteResult
import h4sm.auth.comm.codecs._
import h4sm.auth.domain.tokens._
import h4sm.auth.domain.tokens.AsBaseToken.ops._
import h4sm.petstore.domain._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import tsec.authentication._

case class PetRequest(name: String, bio: Option[String], status: String)

class PetEndpoints[F[_]: Sync: PetAlgebra, T[_]](auth : UserSecuredRequestHandler[F, T])(implicit
  B: AsBaseToken[T[UserId]]
) extends Http4sDsl[F]{

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
 
  def authService = addPet <+> listPets

  def endpoints = auth.liftService(authService)
}
```
*/petstore/infrastructure/endpoints/PetEndpoints.scala*

Note we're using PetAlgebra as a typeclass. This means when it comes time to create these endpoints, we'll need an implicit instance of PetAlgebra[F] available for whatever F gets chosen to run our server with. You'll see how that comes together in the next section. See `petstore/domain/PetAlgebra.scala`. 

This and other endpoints have been defined in `petstore/infrastructure/endpoint/PetEndpoints`, and the endpoints for orders should be reviewed as well.

### About those endpoints...
See the `auth` argument, and that weird `T[_]` type parameter? This allows the user to specify what token type they are using for our authentication. This is what allows these pet endpoints to be used with any kind of authentication we may choose later. `AsBaseToken[T[UserId]]` is stated as a requirement that whatever `T` we choose later, the T must allow us to view the Token as a BaseToken that provides a UserId directly after authenticating the user.


[Now let's write a Server class that will hook these endpoints up.](04-server.md)
