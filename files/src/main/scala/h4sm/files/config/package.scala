package h4sm.files

import cats.mtl.ApplicativeAsk
import h4sm.db.config._

package object config {
  type ConfigAsk[F[_]] = ApplicativeAsk[F, FileConfig]
  type DBConfigAsk[F[_]] = ApplicativeAsk[F, DatabaseConfig]
  type ServerConfigAsk[F[_]] = ApplicativeAsk[F, ServerConfig]
}
