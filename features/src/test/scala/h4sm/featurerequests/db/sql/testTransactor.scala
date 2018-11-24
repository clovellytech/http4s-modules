package h4sm.featurerequests.db
package sql

import cats.effect.IO
import doobie.util.transactor.Transactor
import h4sm.db.config.DatabaseConfig
import h4sm.dbtesting.transactor.getInitializedTransactor

import scala.concurrent.ExecutionContext

object testTransactor {
  implicit lazy val cs = IO.contextShift(ExecutionContext.Implicits.global)

  val cfg = pureconfig.loadConfigOrThrow[DatabaseConfig]("db")

  lazy val testTransactor: Transactor[IO] = getInitializedTransactor(cfg, "ct_auth", "featurerequests").unsafeRunSync()
}
