package com.clovellytech.auth
package domain.users

import java.time.Instant
import java.util.UUID

import cats.data.OptionT
import com.clovellytech.auth.db.domain._
import com.clovellytech.db.CRUDAlgebra

trait UserRepositoryAlgebra[F[_]] extends CRUDAlgebra[F, UUID, User, Instant] {
  def byUsername(username: String) : OptionT[F, (User, UUID, Instant)]
}

