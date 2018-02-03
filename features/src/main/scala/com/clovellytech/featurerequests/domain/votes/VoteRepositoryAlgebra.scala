package com.clovellytech.featurerequests
package domain
package votes

import db.domain._

trait VoteRepositoryAlgebra[F[_]]{
  def put(v: Vote) : F[Unit]
}
