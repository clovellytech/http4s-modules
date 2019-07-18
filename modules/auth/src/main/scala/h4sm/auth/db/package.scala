package h4sm.auth

import cats.effect.Sync
import h4sm.db.config.DatabaseConfig
import javax.sql.DataSource

import scala.util.Try

package object db {
  def initializeDb[M[_]: Sync](ds: DataSource): M[Try[Unit]] =
    DatabaseConfig.initializeDb(ds)("ct_auth")
}
