package h4sm.featurerequests.db
package sql

import cats.effect.IO
import doobie.util.transactor.Transactor

object testTransactor {
  lazy val testTransactor: Transactor[IO] = getTransactor[IO].unsafeRunSync()
}
