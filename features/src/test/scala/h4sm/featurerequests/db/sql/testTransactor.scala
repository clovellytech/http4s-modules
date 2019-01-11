package h4sm.featurerequests.db
package sql

import cats.effect.IO
import doobie.util.transactor.Transactor
import h4sm.db.config._
import h4sm.dbtesting.transactor.getInitializedTransactor
import io.circe.generic.auto._
import scala.concurrent.ExecutionContext

object testTransactor {
  implicit lazy val cs = IO.contextShift(ExecutionContext.Implicits.global)

  val schemaNames = Seq("ct_auth", "ct_feature_requests")

  def getTransactor : IO[Transactor[IO]] =
    loadConfigF[IO, DatabaseConfig]("db")
    .flatMap(getInitializedTransactor(_, schemaNames : _*))

  lazy val testTransactor: Transactor[IO] = getTransactor.unsafeRunSync()
}
