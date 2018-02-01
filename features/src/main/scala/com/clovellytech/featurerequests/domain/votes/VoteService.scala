package com.clovellytech.featurerequests.domain
package votes

import requests._

class VoteService[F[_]](voteRepo : VoteRepositoryAlgebra[F]) {
  def makeVote(v: Vote[FeatureId]) : F[Unit] = voteRepo.create(v)
}
