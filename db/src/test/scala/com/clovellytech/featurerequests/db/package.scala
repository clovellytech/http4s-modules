package com.clovellytech.featurerequests


import cats.effect.IO
import doobie.util.transactor.Transactor

import db.config._

package object db {
  import DatabaseConfig._

  def getTransactor(cfg: DatabaseConfig) : IO[Transactor[IO]] = for {
    tr <- cfg.dbTransactor[IO]
    _ <- initializeDb[IO](tr)
  } yield tr

  lazy val testTransactor : Transactor[IO] = {
    val t: IO[Transactor[IO]] = for {
      cfg <- loadConfig[IO, DatabaseConfig]("db")
      tr <- getTransactor(cfg)
    } yield tr

    t.unsafeRunSync()
  }
}
