package com.clovellytech.files

import cats.implicits._
import cats.effect.{Async, IO, Sync}
import com.clovellytech.db.config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import javax.sql.DataSource

package object infrastructure {
  lazy val db@DatabaseConfig(host, port, user, password, _) = pureconfig.loadConfigOrThrow[DatabaseConfig]("db")

  val schemaNames : List[String] = List(
    "ct_auth",
    "ct_files"
  )

  def initializer[F[_] : Sync](ds : DataSource): F[Unit] =
    schemaNames.map(DatabaseConfig.initializeDb(ds)(_).flatMap(_.toEither.raiseOrPure[F])).reduce(_ *> _)

  def getTransactor[F[_] : Async] : F[Transactor[F]] = for {
    xa <- HikariTransactor.newHikariTransactor[F](db.driver, db.url, user, password)
    _ <- initializer(xa.kernel)
  } yield xa

  lazy val testTransactor: Transactor[IO] = getTransactor[IO].unsafeRunSync()
}
