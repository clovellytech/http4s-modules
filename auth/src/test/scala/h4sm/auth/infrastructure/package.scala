package h4sm
package auth

import cats.effect.IO
import cats.implicits._
import com.typesafe.config.ConfigFactory
import doobie.util.transactor.Transactor
import h4sm.db.config.DatabaseConfig
import io.circe.config.syntax._
import scala.concurrent.ExecutionContext
import h4sm.dbtesting.transactor.getInitializedTransactor

package object infrastructure {
  implicit lazy val cs = IO.contextShift(ExecutionContext.Implicits.global)

  def getTransactor : IO[Transactor[IO]] =
    ConfigFactory
      .load()
      .as[DatabaseConfig]("db")
      .leftWiden[Throwable]
      .raiseOrPure[IO]
      .flatMap(getInitializedTransactor(_, "ct_auth"))

  lazy val testTransactor = getTransactor.unsafeRunSync()
}
