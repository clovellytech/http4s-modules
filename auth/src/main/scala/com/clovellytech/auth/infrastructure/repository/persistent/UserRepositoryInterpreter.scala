package com.clovellytech.auth
package infrastructure.repository.persistent

import cats.data.OptionT
import cats.Monad
import cats.implicits._
import doobie._
import doobie.implicits._
import db.domain.User
import domain.users.UserRepositoryAlgebra
import db.sql._

class UserRepositoryInterpreter[M[_] : Monad](xa : Transactor[M]) extends UserRepositoryAlgebra[M] {

  def insert(a: User): M[Unit] = {
    users.insert(a).run.as(()).exceptSomeSqlState{
      case UNIQUE_VIOLATION => HC.rollback
    }.transact(xa)
  }

  def select: M[List[(User, UserId, Instant)]] = users.select.to[List].transact(xa)

  def byId(id: UserId): OptionT[M, (User, UserId, Instant)] = OptionT(users.selectById(id).option.transact(xa))

  def byUsername(username: String): OptionT[M, (User, UserId, Instant)] =
    OptionT(users.byUsername(username).option.transact(xa))

  def delete(i: UserId): M[Unit] = users.delete(i).run.as(()).transact(xa)

  def safeUpdate(id: UserId, u: User): M[Unit] = users.update(id, u).run.as(()).transact(xa)

  def update(u: User): M[Unit] = (for {
    uq <- byUsername(u.username)
    (user, id, _) = uq
    res <- OptionT(safeUpdate(id, user).map(_.some))
  } yield res).value.map(_.getOrElse(()))
}
