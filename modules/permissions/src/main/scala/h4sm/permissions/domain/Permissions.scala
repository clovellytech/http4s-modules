package h4sm.permissions.domain

import h4sm.auth.UserId


final case class Permission(name: String, description: String, appName: String)

final case class UserPermission[A](uid: UserId, permission: A, grantedBy: UserId)

