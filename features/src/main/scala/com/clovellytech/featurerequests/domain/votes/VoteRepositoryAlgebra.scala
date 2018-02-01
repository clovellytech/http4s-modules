package com.clovellytech.featurerequests
package domain.votes

import db.domain._

trait VoteRepositoryAlgebra[F[_]]{
  def create(v: Vote[FeatureId]) : F[Unit]
}
