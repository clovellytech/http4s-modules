package h4sm.petstore
package domain

import h4sm.auth.UserId
import java.time.Instant

final case class Pet(
  name: String,
  bio: Option[String],
  createdBy: UserId,
  status: String,
  photoUrls: List[String] = Nil,
  updateTime: Option[Instant] = None
)

final case class Order(
  petId: PetId,
  userId: UserId,
  shipTime: Option[Instant] = None
)
