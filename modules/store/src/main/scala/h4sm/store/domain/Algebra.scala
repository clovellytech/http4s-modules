package h4sm.store.domain

import h4sm.db.CRUDAlgebra
import simulacrum.typeclass

@typeclass
trait OrderAlgebra[F[_]] extends CRUDAlgebra[F, OrderId, Order, Instant] {
  def insertOrderItem(orderId: OrderId, orderItem: OrderItem): F[Unit]
  def submitOrder(orderId: OrderId): F[Unit]
}

@typeclass
trait ItemAlgebra[F[_]] extends CRUDAlgebra[F, ItemId, Item, Instant] {
  def byIds(ids: List[ItemId]): F[List[Annotated]]
}
