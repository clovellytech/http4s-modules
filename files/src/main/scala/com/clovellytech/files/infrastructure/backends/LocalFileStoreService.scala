package com.clovellytech.files
package infrastructure.backends

import java.io.File

import cats.effect.Sync
import cats.implicits._
import com.clovellytech.files.config.ConfigAsk
import com.clovellytech.files.db.FileInfoId
import com.clovellytech.files.domain.{FileInfo, FileStoreAlgebra}
import fs2.Stream

class LocalFileStoreService[F[_] : Sync](implicit C : ConfigAsk[F]) extends FileStoreAlgebra[F]{
  def retrieveFile(fileId : FileInfoId) : F[File] = C.ask.map(c => new java.io.File(c.basePath, fileId.toString))

  def retrieve(fileId : FileInfoId): Stream[F, Byte] = Stream.eval(retrieveFile(fileId)).flatMap{ file =>
    fs2.io.file.readAll(file.toPath, 8196)
  }

  def write(fileId: FileInfoId, fileInfo: FileInfo, s: Stream[F, Byte]): F[Unit] = C.ask.flatMap { conf =>
    s.to(fs2.io.file.writeAll[F](new java.io.File(conf.basePath, fileId.toString).toPath)).compile.drain
  }

  def writeAll(fileInfo: FileInfo, ss: Seq[Stream[F, Byte]]): F[Unit] = ???
}
