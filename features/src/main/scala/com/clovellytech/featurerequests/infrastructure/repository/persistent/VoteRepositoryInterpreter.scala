package com.clovellytech.featurerequests
package infrastructure.repository.persistent


import domain.votes._

import cats.Monad
import cats.syntax.functor._
import com.clovellytech.featurerequests.db.domain.Vote
import doobie._
import doobie.implicits._
import doobie.postgres.syntax.monaderror._

import db.domain._
import db.sql.votes._

class VoteRepositoryInterpreter[M[_] : Monad](val xa: Transactor[M]) extends VoteRepositoryAlgebra[M] {
  def getVote(vote: Vote) : M[Option[(VoteId, Vote)]] = select(vote).option.transact(xa)

  def put(v: Vote): M[Unit] = insert(v).run.onUniqueViolation(update(v).run).transact(xa).as(())
}
