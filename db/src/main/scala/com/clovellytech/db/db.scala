package com.clovellytech


import scala.reflect.ClassTag
import cats.effect.Effect
import cats.implicits._
import doobie.util.transactor.Transactor
import com.clovellytech.db.config.DatabaseConfig.initializeDb
import com.clovellytech.db.config._
import pureconfig.ConfigReader


package object db {
  def getTransactor[F[_]: Effect](cfg: DatabaseConfig, schemaName : String) : F[Transactor[F]] = for {
    tr <- cfg.dbTransactor[F]
    _ <- initializeDb[F](tr)(schemaName)
  } yield tr

  def loadTransactorFromConfig[F[_]: Effect, C : ClassTag : ConfigReader](name: String, schemaName: String)(f: C => DatabaseConfig) : F[Transactor[F]] = for {
    cfg <- loadConfig[F, C](name)
    tr <- getTransactor[F](f(cfg), schemaName)
  } yield tr

  def loadTransactorDatabaseConfig[F[_]: Effect](schemaName: String) = loadTransactorFromConfig[F, DatabaseConfig]("db", schemaName)(identity)
}
