package h4sm.files

import cats.effect.IO
import cats.implicits._
import com.typesafe.config.ConfigFactory
import doobie.util.transactor.Transactor
import h4sm.db.config.DatabaseConfig
import h4sm.dbtesting.transactor.getInitializedTransactor
import io.circe.config.syntax._
import io.circe.generic.auto._
import scala.concurrent.ExecutionContext.Implicits.global

package object infrastructure {
  val schemaNames : List[String] = List(
    "ct_auth",
    "ct_files"
  )

  implicit lazy val cs = IO.contextShift(global)

  lazy val testTransactor: Transactor[IO] =
    ConfigFactory
      .load()
      .as[DatabaseConfig]("db")
      .leftWiden[Throwable]
      .raiseOrPure[IO]
      .flatMap(getInitializedTransactor(_, schemaNames : _*))
      .unsafeRunSync()
}
