package h4sm

import cats.mtl.ApplicativeAsk
import h4sm.db.config.DatabaseConfig
import h4sm.files.config.FileConfig

final case class ServerConfig(host: String, port: Int, numThreads: Int)
final case class MainConfig(db: DatabaseConfig, files: FileConfig, server: ServerConfig, test: Boolean)

object MainConfig {
  type ConfigAsk[F[_]] = ApplicativeAsk[F, MainConfig]
}
