package com.clovellytech.featurerequests
package infrastructure.repository

import cats.effect.IO
import doobie.Transactor
import config.FeatureRequestConfig

import db.DatabaseConfig

package object persistent {
  def getTransactor : IO[Transactor[IO]] = for {
    conf <- FeatureRequestConfig.load[IO]
    tr <- DatabaseConfig.dbTransactor[IO](conf.db)
    _ <- DatabaseConfig.initializeDb(conf.db, tr)
  } yield tr

  lazy val testTransactor : Transactor[IO] = getTransactor.unsafeRunSync()
}
