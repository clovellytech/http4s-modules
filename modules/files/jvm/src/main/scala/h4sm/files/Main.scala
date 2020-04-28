package h4sm.files

import cats.ApplicativeError
import cats.effect._
import h4sm.db.config._
import h4sm.files.config._

import scala.concurrent.ExecutionContext.Implicits.global
import io.circe.generic.auto._

trait Configs[F[_]] {
  implicit def ca(implicit F: ApplicativeError[F, Throwable]): ConfigAsk[F] =
    getPureConfigAskPath[F, FileConfig]("files")
  implicit def da(implicit F: ApplicativeError[F, Throwable]): DBConfigAsk[F] =
    getPureConfigAskPath[F, DatabaseConfig]("db")
  implicit def sa(implicit F: ApplicativeError[F, Throwable]): ServerConfigAsk[F] =
    getPureConfigAskPath[F, ServerConfig]("server")
}

object IOServer extends IOApp with Configs[IO] {
  implicit val blk = Blocker.liftExecutionContext(global)
  def run(args: List[String]): IO[ExitCode] = new Server[IO](global).run
}
