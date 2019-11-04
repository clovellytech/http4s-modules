package h4sm
package permissions
package infrastructure
package repository.persistent.sql

import java.time.Instant

import auth.UserId
import db.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import domain._


trait UserPermissionSQL {
  def insert(up: UserPermission[PermissionId]): Update0 = sql"""
    insert
    into ct_permissions.user_permission (user_id, permission_id, granted_by_id)
    values (${up.uid}, ${up.permission}, ${up.grantedBy})
  """.update

  def select: Query0[(UserPermission[PermissionId], UserPermissionId, Instant)] = sql"""
    select user_id, permission_id, granted_by_id, user_permission_id, grant_time
    from ct_permissions.user_permission
  """.query

  def byId(id: UserPermissionId): Query0[(UserPermission[PermissionId], UserPermissionId, Instant)] =
    (select.toFragment ++ fr"where user_permission_id = $id").query

  def userPermission(userId: UserId, appName: String, name: String): Query0[(Permission, PermissionId)] = sql"""
    select name, description, app_name, permission_id
    from ct_permissions.permission
         inner join ct_permissions.user_permission using (permission_id)
    where user_id = $userId and app_name = $appName and name = $name
  """.query

  def insertGetId(up: UserPermission[PermissionId]): ConnectionIO[UserPermissionId] =
    insert(up).withUniqueGeneratedKeys("user_permission_id")

  def delete(id: UserPermissionId): Update0 = sql"""
    delete
    from ct_permissions.user_permission
    where user_permission_id = $id
  """.update
}