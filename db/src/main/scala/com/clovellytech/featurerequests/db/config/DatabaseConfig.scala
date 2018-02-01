package com.clovellytech.featurerequests.db.config

import cats.effect.{Async, Sync}
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

final case class DatabaseConfig(host: String, port: String, user: String, password: String, databaseName: String){
  def driver: String = "org.postgresql.Driver"
  def url : String = s"jdbc:postgresql://$host:$port/$databaseName"

  def dbTransactor[F[_]: Async]: F[HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor[F](driver, url, user, password)
}

object DatabaseConfig {
  /**
    * Runs the flyway migrations against the target database
    */
  def initializeDb[F[_]](xa: HikariTransactor[F])(implicit S: Sync[F]): F[Unit] =
    xa.configure { ds =>
      S.delay {
        val fw = new Flyway()
        fw.setDataSource(ds)
        fw.migrate()
        ()
      }
    }
}
