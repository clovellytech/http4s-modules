package h4sm.auth
package domain.tokens

import h4sm.db.CRUDAlgebra
import simulacrum.typeclass
import tsec.common.SecureRandomId

@typeclass
trait TokenRepositoryAlgebra[F[_]] extends CRUDAlgebra[F, SecureRandomId, BaseToken, Unit] {
  def updateUnique(a: BaseToken): F[Unit]
}
