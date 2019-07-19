package h4sm.auth
package domain.users

import cats.data.OptionT
import h4sm.auth.db.domain._
import h4sm.db.CRUDAlgebra
import simulacrum.typeclass

@typeclass
trait UserRepositoryAlgebra[F[_]] extends CRUDAlgebra[F, UserId, User, Instant] {
  def byUsername(username: String): OptionT[F, Annotated]
  def updateUnique(u: User): F[Unit]
}
