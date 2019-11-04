package h4sm.files.config

final case class FileConfig(
  backend: String,
  basePath: String,
  uploadMax: Int
)

final case class ServerConfig(
  host: String,
  port: Int
)
