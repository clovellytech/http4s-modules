package h4sm.store
package infrastructure.endpoint

import domain._

final case class ItemRequest(
    name: String,
    description: String,
    price: Double,
)

final case class OrderRequest(
    items: Map[ItemId, Int],
)

final case class ViewOrderRequest(
    orderId: OrderId,
)
