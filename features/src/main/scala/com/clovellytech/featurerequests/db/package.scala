package com.clovellytech.featurerequests

import cats.effect.{Async, Sync}
import cats.implicits._
import com.clovellytech.db.config.{DatabaseConfig, loadConfig}
import config.FeatureRequestConfig
import doobie.util.transactor.Transactor
import javax.sql.DataSource

import scala.util.Try

package object db {
  def getTransactor[M[_]: Async] : M[Transactor[M]] = for {
    cfg <- loadConfig[M, FeatureRequestConfig]("featurerequests")
    xa <- cfg.db.dbTransactor[M]
    _ <- initializeAll[M](xa.kernel)
  } yield xa

  def initializeDb[M[_] : Sync](ds: DataSource): M[Try[Unit]] =
    DatabaseConfig.initializeDb(ds)("ct_feature_requests")

  def initializeAll[M[_]: Sync](ds: DataSource): M[Unit] =
    (com.clovellytech.auth.db.initializeDb(ds) *> initializeDb(ds)).as(())
}
