package h4sm
package dbtesting

import java.sql.DriverManager

import cats.effect.{Async, Sync, ContextShift}
import cats.implicits._
import doobie._
import doobie.util.transactor.Transactor
import h4sm.db.config.{DatabaseConfig, loadConfigF}
import io.circe.generic.auto._


object transactor {
  private def createOrDropDb[F[_] : Sync](db : DatabaseConfig, name : String, word : String) : F[Unit] = {
    implicitly[Sync[F]].delay {
      val c = DriverManager.getConnection(s"jdbc:postgresql://${db.host}:${db.port}/", db.user,db.password)
      val statement = c.createStatement
      println(s"$word database $name")
      statement.executeUpdate(s"$word database $name")
      c.close()
    }
  }

  def createDb[F[_] : Sync](db : DatabaseConfig, name : String) : F[Unit] = createOrDropDb(db, name, "create")
  def dropDb[F[_] : Sync](db : DatabaseConfig, name : String) : F[Unit] = createOrDropDb(db, name, "drop")

  def createDb[F[_] : Sync](name : String) : F[Unit] = loadConfigF[F, DatabaseConfig]("db").flatMap(createDb(_, name))
  def dropDb[F[_] : Sync](name : String) : F[Unit] = loadConfigF[F, DatabaseConfig]("db").flatMap(dropDb(_, name))

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

  def getTransactorForDb[F[_] : ContextShift : Async](dbName : String, schemaNames : String*) : F[Transactor[F]] =
    loadConfigF[F, DatabaseConfig]("db")
      .flatMap(cfg =>
        getInitializedTransactor(cfg.copy(databaseName = dbName), schemaNames : _*)
      )
}
