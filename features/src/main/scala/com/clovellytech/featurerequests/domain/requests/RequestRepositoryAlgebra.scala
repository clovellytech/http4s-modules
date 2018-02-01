package com.clovellytech.featurerequests.domain
package requests

import com.clovellytech.featurerequests.db.domain._

trait RequestRepositoryAlgebra[F[_]]{
  def create(r : Feature) : F[Unit]

  def show : F[List[(Feature, Int)]]
}
