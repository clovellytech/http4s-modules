package h4sm.permissions
package domain

import h4sm.auth.UserId
import h4sm.db.CRAlgebra


trait UserPermissionAlgebra[F[_]] extends CRAlgebra[F, UserPermissionId, UserPermission, Unit] {
  def hasPermission(uid : UserId, appName : String, name : String) : F[Boolean]
}
