package h4sm.featurerequests
package infrastructure.repository.persistent


import domain.votes._
import cats.Monad
import cats.data.OptionT
import cats.implicits._
import h4sm.featurerequests.db.domain.Vote
import doobie._
import doobie.implicits._
import doobie.postgres.syntax.monaderror._
import db.domain._
import db.sql._

class VoteRepositoryInterpreter[M[_] : Monad](val xa: Transactor[M]) extends VoteRepositoryAlgebra[M] {
  def getVote(vote: Vote) : M[Option[(VoteId, Vote)]] = votes.select(vote).option.transact(xa)

  def insert(a: Vote): M[Unit] =
    votes.insert(a).run.onUniqueViolation(votes.update(a).run).transact(xa).as(())

  def insertGetId(a: Vote): OptionT[M, VoteId] = OptionT {
    votes.insertGetId(a).map(_.some).onUniqueViolation(HC.rollback.as(none[VoteId])).transact(xa)
  }
}
