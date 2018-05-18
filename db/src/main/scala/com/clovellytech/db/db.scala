package com.clovellytech


import scala.reflect.ClassTag
import cats.effect.Effect
import cats.implicits._
import doobie.util.transactor.Transactor
import com.clovellytech.db.config.DatabaseConfig._
import com.clovellytech.db.config._
import pureconfig.ConfigReader


package object db {
  def getTransactor[F[_]: Effect](cfg: DatabaseConfig, schemaName : String) : F[Transactor[F]] = for {
    xa <- cfg.dbTransactor[F]
    _ <- initializeFromTransactor(xa)(schemaName)
  } yield xa

  def loadTransactorFromConfig[F[_]: Effect, C : ClassTag : ConfigReader](name: String, schemaName: String)(f: C => DatabaseConfig) : F[Transactor[F]] = for {
    cfg <- loadConfig[F, C](name)
    tr <- getTransactor[F](f(cfg), schemaName)
  } yield tr

  def loadTransactorDatabaseConfig[F[_]: Effect](schemaName: String) = loadTransactorFromConfig[F, DatabaseConfig]("db", schemaName)(identity)
}
