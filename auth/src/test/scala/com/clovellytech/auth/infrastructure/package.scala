package com.clovellytech.auth

import cats.effect.IO
import cats.implicits._
import com.clovellytech.db.config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor


package object infrastructure {
  lazy val testTransactor: Transactor[IO] = (for {
    cfg <- pureconfig.loadConfigOrThrow[DatabaseConfig]("db").pure[IO]
    xa <- HikariTransactor.newHikariTransactor[IO](cfg.driver, cfg.url, cfg.user, cfg.password)
    _ <- DatabaseConfig.initializeFromTransactor(xa)("ct_auth")
  } yield xa).unsafeRunSync()
}
