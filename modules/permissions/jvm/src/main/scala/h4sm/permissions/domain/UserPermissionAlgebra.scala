package h4sm.permissions
package domain

import java.time.Instant

import h4sm.auth.UserId
import h4sm.db.CRDAlgebra
import simulacrum.typeclass

@typeclass
trait UserPermissionAlgebra[F[_]]
    extends CRDAlgebra[F, UserPermissionId, UserPermission[PermissionId], Instant] {
  def hasPermission(uid: UserId, appName: String, name: String): F[Boolean]
}
