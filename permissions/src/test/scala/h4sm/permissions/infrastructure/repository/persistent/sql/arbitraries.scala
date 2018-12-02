package h4sm
package permissions.infrastructure.repository.persistent.sql

import java.util.UUID

import h4sm.permissions.domain._
import org.scalacheck._
import dbtesting.arbitraries._

object arbitraries {
  implicit val uuidArb : Arbitrary[UUID] = Arbitrary(Gen.uuid)

  implicit val arbPermission : Arbitrary[Permission] = Arbitrary {
    for {
      name <- nonEmptyString
      description <- nonEmptyString
      appName <- nonEmptyString
    } yield Permission(name, description, appName)
  }

  implicit val userPermissionArb : Arbitrary[UserPermission[PermissionId]] = Arbitrary {
    for {
      uid <- Gen.uuid
      pid <- Gen.uuid
      gby <- Gen.uuid
    } yield UserPermission(uid, pid, gby)
  }
}
