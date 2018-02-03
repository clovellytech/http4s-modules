package com.clovellytech.featurerequests.domain
package requests

import org.joda.time.DateTime

import com.clovellytech.featurerequests.db.domain._

trait RequestRepositoryAlgebra[F[_]]{
  def create(r : Feature) : F[Unit]

  def show : F[List[(FeatureId, Feature, DateTime, Long, Long)]]
}
