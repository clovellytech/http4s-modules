package h4sm.auth
package db

import doobie.Meta

import tsec.common.SecureRandomId
import doobie.postgres.implicits._

package object sql {
  object users extends UserSQL
  object tokens extends BearerSQL

  implicit val userIdMeta = Meta[UserId]
  implicit val instantMeta = Meta[Instant]

  val UNIQUE_VIOLATION = doobie.postgres.sqlstate.class23.UNIQUE_VIOLATION

  implicit val secureRandomIdMeta  : Meta[SecureRandomId] = Meta[Array[Byte]].imap(
    x => SecureRandomId.apply(new String(x))
  )(
    _.getBytes
  )
}
