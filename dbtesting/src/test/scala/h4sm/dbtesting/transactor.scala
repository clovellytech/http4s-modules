package h4sm
package dbtesting

import cats.effect.Async
import cats.implicits._
import doobie._
import doobie.hikari.HikariTransactor
import h4sm.db.config.DatabaseConfig


object transactor {
  def getTransactor[F[_] : Async] : F[HikariTransactor[F]] = for {
    cfg <- pureconfig.loadConfigOrThrow[DatabaseConfig]("db").pure[F]
    xa <- HikariTransactor.newHikariTransactor[F](cfg.driver, cfg.url, cfg.user, cfg.password)
  } yield xa

  def getTransactorInitialized[F[_] : Async](schemaNames : String*): F[Transactor[F]] = for {
    xa <- getTransactor[F]
    initializer = DatabaseConfig.initializeDb[F](xa.kernel) _
    _ <- schemaNames.toList.traverse(initializer)
  } yield xa
}
