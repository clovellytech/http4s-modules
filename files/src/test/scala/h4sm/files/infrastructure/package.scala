package h4sm.files

import cats.effect.IO
import doobie.util.transactor.Transactor
import h4sm.db.config._
import h4sm.dbtesting.transactor.getInitializedTransactor
import io.circe.generic.auto._
import scala.concurrent.ExecutionContext.Implicits.global

package object infrastructure {
  val schemaNames : List[String] = List(
    "ct_auth",
    "ct_files"
  )

  implicit lazy val cs = IO.contextShift(global)

  lazy val testTransactor: Transactor[IO] =
    loadConfigF[IO, DatabaseConfig]("db")
    .flatMap(getInitializedTransactor(_, schemaNames : _*))
    .unsafeRunSync()
}
