package h4sm.files
package domain

import java.io.File

import h4sm.files.db.FileInfoId
import fs2.Stream

trait FileStoreAlgebra[F[_]] {
  def retrieve(fileId: FileInfoId): Stream[F, Byte]
  def retrieveFile(fileId: FileInfoId): F[File]
  def write(fileId: FileInfoId, fileInfo: FileInfo, s: Stream[F, Byte]): F[Unit]
  def writeAll(fileInfo: FileInfo, ss: Seq[Stream[F, Byte]]): F[Unit]
}
