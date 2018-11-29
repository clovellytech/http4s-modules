package h4sm.permissions.infrastructure.repository.persistent.sql

import cats.effect.{Async, ContextShift, IO}
import doobie.util.transactor.Transactor
import h4sm.db.config.DatabaseConfig
import h4sm.dbtesting.transactor.getInitializedTransactor

import scala.concurrent.ExecutionContext

object transactor {
  def getTestTransactor[F[_] : Async : ContextShift](cfg : DatabaseConfig) =
    getInitializedTransactor[F](cfg, "ct_auth", "ct_permissions")

  lazy val cfg = pureconfig.loadConfigOrThrow[DatabaseConfig]("db")

  implicit lazy val cs = IO.contextShift(ExecutionContext.Implicits.global)
  lazy val testTransactor : Transactor[IO] = getTestTransactor[IO](cfg).unsafeRunSync()
}
