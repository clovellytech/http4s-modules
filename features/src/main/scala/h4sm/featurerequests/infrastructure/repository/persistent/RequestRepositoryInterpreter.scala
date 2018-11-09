package h4sm.featurerequests
package infrastructure.repository.persistent

import java.time.Instant

import cats.Monad
import cats.syntax.functor._
import doobie._
import doobie.implicits._
import db.domain._
import db.sql.requests._
import domain.requests._

class RequestRepositoryInterpreter[M[_]: Monad](xa: Transactor[M]) extends RequestRepositoryAlgebra[M] {
  def create(r: Feature): M[Unit] = insert(r).run.as(()).transact(xa)

  def show: M[List[(FeatureId, Feature, Instant, Long, Long)]] = selectAllWithVoteCounts.to[List].transact(xa)
}
