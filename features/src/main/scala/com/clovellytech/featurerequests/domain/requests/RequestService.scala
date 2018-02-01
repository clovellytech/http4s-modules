package com.clovellytech.featurerequests.domain
package requests

class RequestService[F[_]](requestsRepo: RequestRepositoryAlgebra[F]) {
  def makeRequest(request: Feature) : F[Unit] = requestsRepo.create(request)

  def listAll : F[List[(Feature, Int)]] = requestsRepo.show
}
