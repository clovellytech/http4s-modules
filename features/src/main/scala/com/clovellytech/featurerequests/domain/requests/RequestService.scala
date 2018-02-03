package com.clovellytech.featurerequests
package domain.requests

import db.domain._
import org.joda.time.DateTime

class RequestService[F[_]](requestsRepo: RequestRepositoryAlgebra[F]) {
  def makeRequest(request: Feature) : F[Unit] = requestsRepo.create(request)

  def listAll : F[List[(FeatureId, Feature, DateTime, Long, Long)]] = requestsRepo.show
}
