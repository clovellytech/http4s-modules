package com.clovellytech.auth.db

import java.util.UUID

import doobie.Meta
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId
import doobie.postgres.implicits._

package object sql {
  object users extends UserSQL
  object tokens extends BearerSQL

  type Instant = java.time.Instant
  type UserId = UUID

  type BearerToken = TSecBearerToken[UserId]
  val BearerToken = TSecBearerToken

  implicit val userIdMeta = Meta[UserId]
  implicit val instantMeta = Meta[Instant]

  val UNIQUE_VIOLATION = doobie.postgres.sqlstate.class23.UNIQUE_VIOLATION

  implicit val secureRandomIdMeta  : Meta[SecureRandomId] = Meta[Array[Byte]].xmap(
    x => SecureRandomId.apply(new String(x)),
    _.getBytes
  )
}
