package h4sm
package store
package infrastructure.repository.sql

import domain._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import h4sm.auth.db.sql._
import h4sm.auth.comm.authIdTypes._

trait OrderSql {

  type SelectInter = (
      UserId,
      List[ItemId],
      List[Int],
      List[Double],
      Option[Instant],
      Option[Instant],
      Double,
      OrderId,
      Instant,
  )
  type Annotated = (Order, OrderId, Instant)
  val selectAdjust: SelectInter => Annotated = {
    case (a, bs, cs, ds, e, f, g, h, i) =>
      (Order(a, bs.zip(cs).zip(ds).map { case ((a, b), c) => OrderItem(a, b, c) }, e, f, g), h, i)
  }

  def select: Query0[(Order, OrderId, Instant)] = sql"""
    select create_by, 
           array_agg(item_id), 
           array_agg(quantity), 
           array_agg(order_price), 
           submit_date, 
           fulfilled_date, 
           total_price, 
           order_id, 
           create_date
    from (
      select o.order_id as order_id, 
             o.create_by as create_by, 
             o.create_date as create_date, 
             o.submit_date as submit_date,
             o.fulfilled_date as fulfilled_date, 
             o.total_price as total_price, 
             oi.item_id as item_id, 
             oi.order_price as order_price, 
             oi.quantity as quantity
      from ct_store.order o
      inner join ct_store.order_item oi using (order_id)
    ) q
    group by q.create_by, q.submit_date, q.fulfilled_date, q.total_price, q.order_id, q.create_date
  """.query[SelectInter].map(selectAdjust)

  def insert(o: Order): Update0 = sql"""
    insert into ct_store.order (create_by, fulfilled_date, total_price)
    values (${o.createBy}, ${o.fulfilledDate}, ${o.totalPrice})
  """.update

  def insertGetId(o: Order): ConnectionIO[OrderId] = insert(o).withUniqueGeneratedKeys("order_id")

  def insertOrderItem(orderId: OrderId, o: OrderItem): Update0 = sql"""
    insert into ct_store.order_item (order_id, item_id, order_price, quantity)
    values ($orderId, ${o.item}, ${o.orderPrice}, ${o.quantity})
  """.update

  def delete(orderId: OrderId): Update0 = sql"""
    delete from ct_store.order
    where order_id = $orderId
  """.update

  def byId(orderId: OrderId): Query0[Annotated] = (select.toFragment ++ fr"""
    where order_id = $orderId
  """).query[SelectInter].map(selectAdjust)

  def update(orderId: OrderId, order: Order): Update0 = sql"""
    update ct_store.order
    set fulfilled_date = ${order.fulfilledDate},
        total_price = ${order.totalPrice}
    where order_id = $orderId
  """.update

  def setSubmitted(orderId: OrderId): Update0 = sql"""
    update ct_store.order
    set submit_date = now()
    where order_id = $orderId
  """.update
}
