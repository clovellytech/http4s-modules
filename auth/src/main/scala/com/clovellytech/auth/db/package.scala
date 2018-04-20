package com.clovellytech.auth

import cats.effect.Sync
import doobie.hikari.HikariTransactor

import com.clovellytech.db.config.DatabaseConfig

package object db {
  def initializeDb[M[_] : Sync](xa: HikariTransactor[M]): M[Unit] =
    DatabaseConfig.initializeDb(xa)("ct_auth")
}
