package h4sm.featurerequests.db
package sql

import cats.syntax.either._
import cats.effect.IO
import com.typesafe.config.ConfigFactory
import doobie.util.transactor.Transactor
import h4sm.db.config.DatabaseConfig
import h4sm.dbtesting.transactor.getInitializedTransactor
import io.circe.config.syntax._
import io.circe.generic.auto._
import scala.concurrent.ExecutionContext

object testTransactor {
  implicit lazy val cs = IO.contextShift(ExecutionContext.Implicits.global)

  def getTransactor : IO[Transactor[IO]] =
    ConfigFactory
      .load()
      .as[DatabaseConfig]("db")
      .leftMap(_.asInstanceOf[Throwable])
      .raiseOrPure[IO]
      .flatMap(getInitializedTransactor(_, "ct_auth", "featurerequests"))

  lazy val testTransactor: Transactor[IO] = getTransactor.unsafeRunSync()
}
