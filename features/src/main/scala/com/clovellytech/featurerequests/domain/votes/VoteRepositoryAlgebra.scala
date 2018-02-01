package com.clovellytech.featurerequests.domain
package votes

import requests._

trait VoteRepositoryAlgebra[F[_]]{
  def create(v: Vote[FeatureId]) : F[Unit]
}
