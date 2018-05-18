package com.clovellytech.auth

import cats.effect.Sync
import com.clovellytech.db.config.DatabaseConfig
import javax.sql.DataSource

import scala.util.Try

package object db {
  def initializeDb[M[_] : Sync](ds : DataSource): M[Try[Unit]] =
    DatabaseConfig.initializeDb(ds)("ct_auth")
}
