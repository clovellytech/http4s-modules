package h4sm.petstore

import h4sm.db.config.DatabaseConfig

final case class ServerConfig(host: String, port: Int, numThreads: Int)
final case class MainConfig(db: DatabaseConfig, server: ServerConfig)
