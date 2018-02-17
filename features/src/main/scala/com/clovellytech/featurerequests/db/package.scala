package com.clovellytech.featurerequests

import cats.implicits._
import cats.effect.{Async, Effect}
import com.clovellytech.featurerequests.config.FeatureRequestConfig
import db.config.loadConfig
import db.config.DatabaseConfig.initializeDb
import doobie.util.transactor.Transactor

package object db {
  def getTransactor[F[_]: Async : Effect] : F[Transactor[F]] = for {
    cfg <- loadConfig[F, FeatureRequestConfig]("featurerequests")
    tr <- cfg.db.dbTransactor[F]
    _ <- initializeDb[F](tr)
  } yield tr
}
