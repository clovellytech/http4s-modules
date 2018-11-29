package h4sm
package permissions.infrastructure.repository.persistent.sql

import h4sm.permissions.domain.Permission
import org.scalacheck._
import dbtesting.arbitraries._

object arbitraries {
  implicit val arbPermission : Arbitrary[Permission] = Arbitrary {
    for {
      name <- nonEmptyString
      description <- nonEmptyString
      appName <- nonEmptyString
    } yield Permission(name, description, appName)
  }
}
