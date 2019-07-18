package h4sm.store

import h4sm.auth.UserId

package object domain {
  type OrderId = java.util.UUID
  type ItemId = java.util.UUID
  type Instant = java.time.Instant

  type Order = Order0[UserId, ItemId]
  type Item = Item0[UserId]
  type OrderItem = OrderItem0[ItemId]
  val Order = Order0
  val Item = Item0
  val OrderItem = OrderItem0
}
