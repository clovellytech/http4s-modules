package h4sm
package permissions
package infrastructure.repository.persistent.sql

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import domain.{Permission, PermissionId}
import db.implicits._

trait PermissionSQL {

  def select: Query0[(Permission, PermissionId)] = sql"""
    select name, description, app_name, permission_id
    from ct_permissions.permission
  """.query

  def byId(id: PermissionId): Query0[Permission] = (select.toFragment ++ fr"""
    where permission_id = $id
  """).query[(Permission, PermissionId)].map(_._1)

  def byAttributes(appName: String, name: String): Query0[(Permission, PermissionId)] = (select.toFragment ++ fr"""
    where app_name = $appName and name = $name
  """).query

  def byAppName(appName: String): Query0[(Permission, PermissionId)] = (select.toFragment ++ fr"""
    where app_name = $appName
  """).query

  def insert(p: Permission): Update0 = sql"""
    insert into ct_permissions.permission (name, description, app_name)
    values (${p.name}, ${p.description}, ${p.appName})
  """.update

  def safeUpdate(pid: PermissionId, p: Permission): Update0 = sql"""
    update ct_permissions.permission
    set name = ${p.name}, description = ${p.description}
    where permission_id = $pid
  """.update

  def delete(pid: PermissionId): Update0 = sql"""
    delete
    from ct_permissions.permission
    where permission_id = $pid
  """.update

  def insertGetId(p: Permission): ConnectionIO[PermissionId] = insert(p).withUniqueGeneratedKeys("permission_id")
}
