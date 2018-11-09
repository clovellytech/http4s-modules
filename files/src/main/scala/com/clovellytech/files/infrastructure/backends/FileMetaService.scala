package com.clovellytech
package files
package infrastructure.backends

import cats.Monad
import cats.implicits._
import com.clovellytech.files.db._
import domain._
import db.sql._
import doobie._
import doobie.implicits._

class FileMetaService[F[_] : Monad](xa : Transactor[F]) extends FileMetaAlgebra[F] {

  def storeMeta(fileInfo: FileInfo): F[FileInfoId] = files.insertGenId(fileInfo).transact(xa)

  def retrieveMeta(fileId: FileInfoId): F[FileInfo] = files.selectById(fileId).unique.transact(xa)

  def retrieveUserMeta(ownerId: FileInfoId): F[List[(FileInfoId, FileInfo)]] =
    files.selectFiles(ownerId).to[List].transact(xa)

  def updateFileSaveTime(fileId: FileInfoId): F[Unit] = files.updateFileUploadTime(fileId).run.transact(xa).as(())
}
