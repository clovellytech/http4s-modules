package h4sm.auth
package domain.users

import java.time.Instant
import java.util.UUID

import cats.data.OptionT
import h4sm.auth.db.domain._
import h4sm.db.CRUDAlgebra
import simulacrum.typeclass

@typeclass
trait UserRepositoryAlgebra[F[_]] extends CRUDAlgebra[F, UUID, User, Instant] {
  def byUsername(username: String) : OptionT[F, (User, UUID, Instant)]
}

