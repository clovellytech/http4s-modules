package com.clovellytech.featurerequests
package infrastructure.repository.persistent

import domain.requests._

import cats.Monad
import cats.syntax.functor._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

object RequestSQL {
  def insert(request: Feature) : Update0 = sql"""
    insert into feature_request (requesting_user_id, title, description)
    values (${request.userId}, ${request.title}, ${request.description})
  """.update

  def selectAll : Query0[Feature] = sql"""
    select requesting_user_id, title, description, create_date
    from feature_requests
  """.query

  def selectAllWithVoteCounts : Query0[(Feature, Int)] = sql"""
    select fs.requesting_user_id, fs.title, fs.description, fs.create_date, count(vote_id)
    from feature_requests fs
    left outer join vote vs using (feature_request_id)
    group by feature_request_id
    order by fs.create_date
  """.query
}

class RequestRepositoryInterpreter[M[_]: Monad](xa: Transactor[M]) extends RequestRepositoryAlgebra[M] {
  import RequestSQL._

  def create(r: Feature): M[Unit] = insert(r).run.as(()).transact(xa)

  def show: M[List[(Feature, Int)]] = selectAllWithVoteCounts.list.transact(xa)
}
