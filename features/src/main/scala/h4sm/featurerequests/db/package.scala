package h4sm.featurerequests

import cats.effect.{Async, Sync}
import cats.implicits._
import h4sm.db.config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import javax.sql.DataSource

import scala.util.Try

package object db {
  def getTransactor[M[_]: Async] : M[Transactor[M]] = for {
    cfg <- pureconfig.loadConfigOrThrow[DatabaseConfig]("db").pure[M]
    xa <- HikariTransactor.newHikariTransactor[M](cfg.driver, cfg.url, cfg.user, cfg.password)
    _ <- initializeAll[M](xa.kernel)
  } yield xa

  def initializeDb[M[_] : Sync](ds: DataSource): M[Try[Unit]] =
    DatabaseConfig.initializeDb(ds)("ct_feature_requests")

  def initializeAll[M[_]: Sync](ds: DataSource): M[Unit] =
    (h4sm.auth.db.initializeDb(ds) *> initializeDb(ds)).as(())
}
