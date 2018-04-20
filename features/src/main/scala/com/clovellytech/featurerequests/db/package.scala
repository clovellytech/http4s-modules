package com.clovellytech.featurerequests

import cats.effect.{Effect, Sync}
import cats.implicits._
import com.clovellytech.db.config.{DatabaseConfig, loadConfig}
import com.clovellytech.featurerequests.config.FeatureRequestConfig
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor

package object db {
  def getTransactor[M[_]: Effect] : M[Transactor[M]] = for {
    cfg <- loadConfig[M, FeatureRequestConfig]("featurerequests")
    xa <- cfg.db.dbTransactor[M]
    _ <- initializeAll[M](xa)
  } yield xa

  def initializeDb[M[_] : Sync](xa: HikariTransactor[M]): M[Unit] =
    DatabaseConfig.initializeDb(xa)("ct_feature_requests")

  def initializeAll[M[_]: Sync](xa: HikariTransactor[M]): M[Unit] =
    com.clovellytech.auth.db.initializeDb[M](xa) *> initializeDb(xa)
}
