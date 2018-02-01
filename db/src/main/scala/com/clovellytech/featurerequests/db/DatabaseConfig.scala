package com.clovellytech.featurerequests.db

import cats.effect.{Async, Sync}
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

final case class DatabaseConfig(host: String, port: String, user: String, password: String, databaseName: String){
  def driver: String = "org.postgresql.Driver"
  def url : String = s"jdbc:postgresql://$host:$port/$databaseName"
}

object DatabaseConfig {

  def dbTransactor[F[_]: Async](dbConfig: DatabaseConfig): F[HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor[F](dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.password)

  /**
    * Runs the flyway migrations against the target database
    *
    * This only gets applied if the database is H2, our local in-memory database.  Otherwise
    * we skip this step
    */
  def initializeDb[F[_]](dbConfig: DatabaseConfig, xa: HikariTransactor[F])(implicit S: Sync[F]): F[Unit] =
    xa.configure { ds =>
      S.delay {
        val fw = new Flyway()
        fw.setDataSource(ds)
        fw.migrate()
        ()
      }
    }
}
