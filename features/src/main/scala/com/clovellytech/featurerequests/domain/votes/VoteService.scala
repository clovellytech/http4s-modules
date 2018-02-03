package com.clovellytech.featurerequests
package domain.votes

import db.domain._

class VoteService[F[_]](voteRepo : VoteRepositoryAlgebra[F]) {
  def makeVote(v: Vote) : F[Unit] = voteRepo.put(v)
}
