package h4sm.permissions.infrastructure.repository.persistent.sql

import cats.effect.{Async, ContextShift, IO}
import cats.implicits._
import com.typesafe.config.ConfigFactory
import doobie.util.transactor.Transactor
import h4sm.db.config.DatabaseConfig
import h4sm.dbtesting.transactor.getInitializedTransactor
import io.circe.config.syntax._
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext

object transactor {
  def getTestTransactor[F[_] : Async : ContextShift]: F[doobie.Transactor[F]] =
    ConfigFactory
      .load()
      .as[DatabaseConfig]("db")
      .leftMap(_.asInstanceOf[Throwable])
      .raiseOrPure[F]
      .flatMap(getInitializedTransactor[F](_, "ct_auth", "ct_permissions"))

  implicit lazy val cs = IO.contextShift(ExecutionContext.Implicits.global)
  lazy val testTransactor : Transactor[IO] = getTestTransactor[IO].unsafeRunSync()
}
