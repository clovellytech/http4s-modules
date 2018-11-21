package h4sm
package auth

import cats.effect.IO
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext
import h4sm.db.config.DatabaseConfig
import h4sm.dbtesting.transactor.getInitializedTransactor

package object infrastructure {
  implicit lazy val cs = IO.contextShift(ExecutionContext.Implicits.global)

  val cfg = pureconfig.loadConfigOrThrow[DatabaseConfig]("db")

  lazy val testTransactor: Transactor[IO] = getInitializedTransactor(cfg, "ct_auth").unsafeRunSync()
}
