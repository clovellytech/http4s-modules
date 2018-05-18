package com.clovellytech.db.config

import scala.util.Try
import cats.effect.{Async, Sync}
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import javax.sql.DataSource
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException

final case class DatabaseConfig(
  host: String,
  port: String,
  user: String,
  password: String,
  databaseName: String
){
  def driver: String = "org.postgresql.Driver"
  def url : String = s"jdbc:postgresql://$host:$port/$databaseName"

  def dbTransactor[M[_]: Async]: M[HikariTransactor[M]] =
    HikariTransactor.newHikariTransactor[M](driver, url, user, password)
}

object DatabaseConfig {
  /**
    * Runs the flyway migrations against the target database
    */
  def initializeDb[M[_] : Sync](ds: DataSource)(schemaName: String): M[Try[Unit]] = Sync[M].delay {
    val fw = new Flyway()
    fw.setDataSource(ds)
    fw.setSchemas(schemaName)
    fw.setLocations(s"db/$schemaName/migration")
    Try{
      fw.migrate()
      ()
    }.recoverWith{
      case e : FlywayException =>
        println("Got flyway exception")
        println(e)
        println("Attempting to recover.")
        Try {
          fw.repair()
          fw.migrate()
          ()
        }.recover{
          case e : FlywayException =>
            println("Recovery failed")
            println(e)
            ()
        }
    }
  }

  def initializeFromTransactor[M[_] : Sync, A <: DataSource](xa: Transactor.Aux[M, A])(
    schemaName: String
  ): M[Try[Unit]] = initializeDb(xa.kernel)(schemaName)

}
