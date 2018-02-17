package com.clovellytech.featurerequests.db
package sql

import cats.effect.{Async, IO}
import config.{DatabaseConfig, loadConfig}
import doobie.util.transactor.Transactor

object testTransactor {
  def configTransactor[F[_]: Async] : F[Transactor[F]] = {
    val t: F[Transactor[F]] = for {
      cfg <- loadConfig[F, DatabaseConfig]("db")
      tr <- getTransactor[F](cfg)
    } yield tr
  }

  def getTestTransactor : IO[Transactor[IO]] = configTransactor[IO]

  lazy val testTransactor: Transactor[IO] = getTestTransactor.unsafeRunSync()
}
