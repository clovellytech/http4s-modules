package com.clovellytech.featurerequests

import cats.implicits._
import cats.effect.Async
import db.config.DatabaseConfig
import db.config.DatabaseConfig.initializeDb
import doobie.util.transactor.Transactor

package object db {
  def getTransactor[F[_]: Async](cfg: DatabaseConfig) : F[Transactor[F]] = for {
    tr <- cfg.dbTransactor[F]
    _ <- initializeDb[F](tr)
  } yield tr
}
