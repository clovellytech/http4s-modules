package h4sm.files

import cats.Applicative
import cats.effect._
import h4sm.db.config.DatabaseConfig
import h4sm.files.config._
import scala.concurrent.ExecutionContext.Implicits.global

trait Configs[F[_]]{
  implicit def ca(implicit F : Applicative[F]) : ConfigAsk[F] = config.getConfigAsk[F, FileConfig]("files")
  implicit def da(implicit F : Applicative[F]) : DBConfigAsk[F] = config.getConfigAsk[F, DatabaseConfig]("db")
  implicit def sa(implicit F : Applicative[F]) : ServerConfigAsk[F] = config.getConfigAsk[F, ServerConfig]("server")
}


object IOServer extends IOApp with Configs[IO] {
  def run(args: List[String]): IO[ExitCode] = new Server[IO].run
}
