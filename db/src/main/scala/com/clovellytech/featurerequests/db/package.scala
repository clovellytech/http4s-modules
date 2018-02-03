package com.clovellytech.featurerequests

import cats.effect.IO
import db.config.DatabaseConfig
import db.config.DatabaseConfig.initializeDb
import doobie.util.transactor.Transactor

package object db {
  def getTransactor(cfg: DatabaseConfig) : IO[Transactor[IO]] = for {
    tr <- cfg.dbTransactor[IO]
    _ <- initializeDb[IO](tr)
  } yield tr
}
