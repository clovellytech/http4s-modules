package h4sm.auth
package db.sql

import doobie._
import doobie.implicits._
import tsec.common.SecureRandomId


trait BearerSQL {
  def select : Query0[BearerToken] = sql"""
    select secure_id, user_id, expiry, last_touched
    from ct_auth.token
  """.query

  def byUserId(userId: UserId) : Query0[BearerToken] = (
    select.toFragment ++ sql"""
    where user_id = $userId
  """).query

  def byId(secureId: SecureRandomId) : Query0[BearerToken] = (
    select.toFragment ++ sql"""
    where secure_id = $secureId
  """).query

  def byUsername(userId: String) : Query0[BearerToken] = (
    select.toFragment ++ sql"""
    where user_id = $userId
  """).query

  def insert(u: BearerToken) : Update0 = sql"""
    insert into ct_auth.token (secure_id, user_id, expiry, last_touched)
    values (${u.id}, ${u.identity}, ${u.expiry}, ${u.lastTouched})
  """.update

  def update(id: SecureRandomId, token : BearerToken): Update0 = sql"""
    update ct_auth.token
    set last_touched = ${token.expiry}
    where secure_id = $id
  """.update

  def delete(id: SecureRandomId) : Update0 = sql"""
    delete from ct_auth.token
    where secure_id = $id
  """.update
}
