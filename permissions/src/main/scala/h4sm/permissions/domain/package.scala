package h4sm.permissions

import java.util.UUID

import h4sm.auth.UserId

package object domain {
  type PermissionId = UUID
  type UserPermission = (UserId, PermissionId)

  type UserPermissionId = UUID
}
