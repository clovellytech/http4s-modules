package h4sm.featurerequests.db
package sql

import cats.effect.IO
import doobie.util.transactor.Transactor
import h4sm.db.config._
import h4sm.dbtesting.transactor.getInitializedTransactor
import io.circe.generic.auto._
import scala.concurrent.ExecutionContext
import java.sql.DriverManager

object testTransactor {
  implicit lazy val cs = IO.contextShift(ExecutionContext.Implicits.global)

  val schemaNames = Seq("ct_auth", "ct_feature_requests")

  private def createOrDropDb(db : DatabaseConfig, name : String, word : String) : IO[Unit] = {
    IO.delay {
      val c = DriverManager.getConnection(s"jdbc:postgresql://${db.host}:${db.port}/", db.user,db.password)
      val statement = c.createStatement
      println(s"$word database $name")
      statement.executeUpdate(s"$word database $name")
      c.close()
    }
  }

  def createDb(db : DatabaseConfig, name : String) : IO[Unit] = createOrDropDb(db, name, "create")
  def dropDb(db : DatabaseConfig, name : String) : IO[Unit] = createOrDropDb(db, name, "drop")

  def createDb(name : String) : IO[Unit] = loadConfigF[IO, DatabaseConfig]("db").flatMap(createDb(_, name))
  def dropDb(name : String) : IO[Unit] = loadConfigF[IO, DatabaseConfig]("db").flatMap(dropDb(_, name))

  def getTransactor : IO[Transactor[IO]] =
    loadConfigF[IO, DatabaseConfig]("db")
    .flatMap(getInitializedTransactor(_, schemaNames : _*))

  def getTransactorForDb(dbName : String) : IO[Transactor[IO]] =
    loadConfigF[IO, DatabaseConfig]("db")
    .flatMap(cfg =>
      getInitializedTransactor(cfg.copy(databaseName = dbName), schemaNames : _*)
    )

  lazy val testTransactor: Transactor[IO] = getTransactor.unsafeRunSync()
}
