package h4sm.permissions.domain

import cats.data.OptionT
import h4sm.db.CRUDAlgebra
import simulacrum.typeclass

@typeclass
trait PermissionAlgebra[F[_]] extends CRUDAlgebra[F, PermissionId, Permission, Unit]{
  def selectByAppName(appName : String) : F[List[(Permission, PermissionId)]]
  def selectByAttributes(appName : String, name : String) : OptionT[F, (Permission, PermissionId)]
}
