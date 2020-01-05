package h4sm.petstore
package db.sql

import domain._
import doobie._
import doobie.implicits._
import h4sm.auth.db.sql._ 
import h4sm.auth.comm.authIdTypes._

trait OrderSql {
  def insert(a: Order): Update0 = sql"""
    insert into ct_petstore.order (pet_id, user_id)
    values (${a.petId}, ${a.userId})
  """.update

  def insertGetId(a: Order): ConnectionIO[OrderId] = insert(a).withUniqueGeneratedKeys("order_id")

  def select: Query0[(Order, OrderId, Instant)] = sql"""
    select pet_id, user_id, ship_time, order_id, create_time
    from ct_petstore.order
  """.query

  def selectById(id: OrderId): Query0[(Order, OrderId, Instant)] = (select.toFragment ++ fr"""
    where order_id = $id
  """).query

  def setShipped(orderId: OrderId): Update0 = sql"""
    update ct_petstore.order
    set ship_time = now()
    where order_id = $orderId
  """.update

  def delete(orderId: OrderId): Update0 = sql"""
    delete from ct_petstore.order
    where order_id = $orderId
  """.update
}
