---
id:      endpoints
title:   "Http4s Modules by Example - Build Endpoints"
---

Now that our algebras are complete for storing and querying pets and orders, let us work through adding endpoints for interacting with these data.

Eventually we will need to handle permissions, to allow only employees of the petstore to add pets and update ship dates on orders. For now let's implement the endpoints only requiring registered users.

```scala mdoc

import cats.effect.Sync
import org.http4s._
import tsec.authentication._

class PetEndpoints[F[_]: Sync](){


  def addPet: 
}

```