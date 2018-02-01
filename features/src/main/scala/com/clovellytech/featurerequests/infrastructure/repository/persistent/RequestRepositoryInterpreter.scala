package com.clovellytech.featurerequests
package infrastructure.repository.persistent

import domain.requests._
import cats.Monad
import cats.syntax.functor._
import com.clovellytech.featurerequests.db.domain.Feature
import doobie._
import doobie.implicits._

import db.sql.requests._

class RequestRepositoryInterpreter[M[_]: Monad](xa: Transactor[M]) extends RequestRepositoryAlgebra[M] {
  def create(r: Feature): M[Unit] = insert(r).run.as(()).transact(xa)

  def show: M[List[(Feature, Int)]] = selectAllWithVoteCounts.list.transact(xa)
}
