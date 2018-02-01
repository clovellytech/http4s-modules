package com.clovellytech.featurerequests.domain
package requests

trait RequestRepositoryAlgebra[F[_]]{
  def create(r : Feature) : F[Unit]

  def show : F[List[(Feature, Int)]]
}
