package com.clovellytech.featurerequests
package domain.requests

import java.time.Instant

import db.domain._

class RequestService[F[_]](requestsRepo: RequestRepositoryAlgebra[F]) {
  def makeRequest(request: Feature) : F[Unit] = requestsRepo.create(request)

  def listAll : F[List[(FeatureId, Feature, Instant, Long, Long)]] = requestsRepo.show
}
