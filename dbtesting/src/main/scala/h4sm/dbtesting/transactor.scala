package h4sm
package dbtesting

import cats.effect.{Async, ContextShift}
import cats.implicits._
import doobie._
import h4sm.db.config.DatabaseConfig


object transactor {
  def getTransactor[F[_] : Async : ContextShift] (cfg : DatabaseConfig) : Transactor[F] =
    Transactor.fromDriverManager[F](
      cfg.driver, // driver classname
      cfg.url, // connect URL (driver-specific)
      cfg.user,              // user
      cfg.password           // password
    )

  def getInitializedTransactor[F[_] : ContextShift](cfg : DatabaseConfig, schemaNames : String*)(implicit
    F : Async[F]
  ) : F[Transactor[F]] =
    DatabaseConfig.initialize[F](cfg)(schemaNames : _*) *> F.delay(getTransactor[F](cfg))
}
