package h4sm.store.domain

final case class Item0[A](
  title: String,
  description: String,
  createBy: A,
  price: Double
)

final case class OrderItem0[A](
  item: A,
  quantity: Int,
  orderPrice: Double
)

final case class Order0[A, B](
  createBy: A,
  items: List[OrderItem0[B]],
  submitDate: Option[Instant],
  fulfilledDate: Option[Instant],
  totalPrice: Double
)
