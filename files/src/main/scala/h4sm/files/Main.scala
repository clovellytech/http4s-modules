package h4sm.files

import cats.effect._
import cats.data.EitherT
import h4sm.db.config.DatabaseConfig
import h4sm.files.Mains.ServerEffect
import h4sm.files.config._
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode

object Mains{
  type ServerEffect[A] = EitherT[IO, Throwable, A]

  implicit val ca : ConfigAsk[ServerEffect] = config.getConfigAsk[ServerEffect, FileConfig]("files")
  implicit val da : DBConfigAsk[ServerEffect] = config.getConfigAsk[ServerEffect, DatabaseConfig]("db")
  implicit val sa : ServerConfigAsk[ServerEffect] = config.getConfigAsk[ServerEffect, ServerConfig]("server")
}

import Mains._

object IOServer extends StreamApp[ServerEffect] with Server[ServerEffect]{
  import concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], shutdown: ServerEffect[Unit]): Stream[ServerEffect, ExitCode] =
    createStream(shutdown)
}
