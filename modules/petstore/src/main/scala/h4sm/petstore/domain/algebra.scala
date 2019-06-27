package h4sm.petstore
package domain

import h4sm.db._
import java.time.Instant
import simulacrum._

@typeclass
trait PetAlgebra[F[_]] extends CRUDAlgebra[F, PetId, Pet, Instant]

@typeclass
trait OrderAlgebra[F[_]] extends CRDAlgebra[F, OrderId, Order, Instant] {
  def setShipped(orderId: OrderId): F[Unit]
}
