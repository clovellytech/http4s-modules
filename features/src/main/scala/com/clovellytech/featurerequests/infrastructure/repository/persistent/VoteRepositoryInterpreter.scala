package com.clovellytech.featurerequests
package infrastructure.repository.persistent


import domain.votes._
import domain.requests._

import cats.Monad
import cats.syntax.functor._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

object VoteSQL {
  def insert(vote: Vote[FeatureId]) : Update0 = sql"""
    insert into vote (feature_request_id, by_user_id, vote, comment)
    values (${vote.featureRequest}, ${vote.userId}, ${vote.vote}, ${vote.comment})
  """.update
}

class VoteRepositoryInterpreter[M[_] : Monad](val xa: Transactor[M]) extends VoteRepositoryAlgebra[M] {
  import VoteSQL._

  def create(v: Vote[FeatureId]): M[Unit] = insert(v).run.transact(xa).as(())
}
