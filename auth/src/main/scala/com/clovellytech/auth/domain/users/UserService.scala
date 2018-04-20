package com.clovellytech.auth
package domain.users

import java.time.Instant
import java.util.UUID

import cats.data.OptionT
import com.clovellytech.db.CRUDService
import db.domain._

class UserService[F[_]](val algebra : UserRepositoryAlgebra[F]) extends CRUDService[F, UUID, User, Instant]{
  type Alg = UserRepositoryAlgebra[F]

  def byUsername(username: String) : OptionT[F, (User, UUID, Instant)] = algebra.byUsername(username)
}
