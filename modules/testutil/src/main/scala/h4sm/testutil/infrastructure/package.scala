package h4sm
package testutil

import cats.effect.IO
import h4sm.db.config.DatabaseConfig
import io.circe.config._
import scala.concurrent.ExecutionContext

package object infrastructure {
  implicit lazy val cs = IO.contextShift(ExecutionContext.Implicits.global)

  def dbConfig: IO[DatabaseConfig] = parser.decodePathF[IO, DatabaseConfig]("db")
}
