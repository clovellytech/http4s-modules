package com.clovellytech.auth
package db.sql

import doobie._
import doobie.implicits._
import db.domain._

trait UserSQL {
  def select : Query0[(User, UserId, Instant)] = sql"""
    select username, hash, user_id, join_date
    from ct_auth.user
  """.query

  def selectById(id: UserId) : Query0[(User, UserId, Instant)] = (
    select.toFragment ++ sql"""
    where user_id = $id
  """).query

  def byUsername(username: String) : Query0[(User, UserId, Instant)] = (
    select.toFragment ++ sql"""
    where username = $username
  """).query

  def insert(u: User) : Update0 = sql"""
    insert into ct_auth.user (username, hash)
    values (${u.username}, ${u.hash})
  """.update

  def update(id: UserId, u: User): Update0 = sql"""
    update ct_auth.user
    set hash = ${u.hash}
    where user_id = $id
  """.update

  def delete(id: UserId) : Update0 = sql"""
    delete from ct_auth.user
    where user_id = $id
  """.update
}
