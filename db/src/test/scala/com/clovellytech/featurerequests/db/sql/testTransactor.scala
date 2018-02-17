package com.clovellytech.featurerequests.db
package sql

import cats.effect.IO
import config.{DatabaseConfig, loadConfig}
import doobie.util.transactor.Transactor

object testTransactor {
  lazy val testTransactor : Transactor[IO] = {
    val t: IO[Transactor[IO]] = for {
      cfg <- loadConfig[IO, DatabaseConfig]("db")
      tr <- getTransactor(cfg)
    } yield tr

    t.unsafeRunSync()
  }
}