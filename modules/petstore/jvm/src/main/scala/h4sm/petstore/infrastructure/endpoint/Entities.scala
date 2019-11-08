package h4sm.petstore
package infrastructure.endpoint

import domain._

final case class PetRequest(
    name: String,
    bio: Option[String],
    status: String,
)

final case class OrderRequest(
    petId: PetId,
)
