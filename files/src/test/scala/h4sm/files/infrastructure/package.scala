package h4sm.files

import cats.effect.IO
import h4sm.db.config.DatabaseConfig
import doobie.util.transactor.Transactor
import h4sm.dbtesting.transactor.getInitializedTransactor
import scala.concurrent.ExecutionContext.Implicits.global

package object infrastructure {
  lazy val db@DatabaseConfig(host, port, user, password, _) = pureconfig.loadConfigOrThrow[DatabaseConfig]("db")

  val schemaNames : List[String] = List(
    "ct_auth",
    "ct_files"
  )

  implicit lazy val cs = IO.contextShift(global)

  lazy val testTransactor: Transactor[IO] = getInitializedTransactor(db, schemaNames : _*).unsafeRunSync()
}
