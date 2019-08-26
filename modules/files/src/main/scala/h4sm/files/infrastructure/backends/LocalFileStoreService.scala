package h4sm.files
package infrastructure.backends

import java.io.File

import cats.effect.{Blocker, ContextShift, Sync}
import cats.implicits._
import h4sm.files.config.ConfigAsk
import h4sm.files.domain._
import fs2.Stream

class LocalFileStoreService[F[_]: Sync: ContextShift](
  implicit C: ConfigAsk[F], blk: Blocker
) extends FileStoreAlgebra[F]{
  def retrieveFile(fileId: FileInfoId): F[File] = C.ask.map(c => new java.io.File(c.basePath, fileId.toString))

  def retrieve(fileId: FileInfoId): Stream[F, Byte] = Stream.eval(retrieveFile(fileId)).flatMap{ file =>
    fs2.io.file.readAll(file.toPath, blk, 8196)()
  }

  def write(fileId: FileInfoId, fileInfo: FileInfo, s: Stream[F, Byte]): F[Unit] = C.ask.flatMap { conf =>
    s.through(fs2.io.file.writeAll[F](new java.io.File(conf.basePath, fileId.toString).toPath, blk)).compile.drain
  }

  def writeAll(fileInfo: FileInfo, ss: Seq[Stream[F, Byte]]): F[Unit] = ???
}
