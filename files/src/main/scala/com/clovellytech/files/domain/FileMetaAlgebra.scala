package com.clovellytech
package files
package domain

import cats.syntax.functor._
import cats.Functor
import com.clovellytech.auth.UserId
import db.FileInfoId

trait FileMetaAlgebra[F[_]]{
  def storeMeta(fileInfo : FileInfo) : F[FileInfoId]

  def retrieveMeta(fileId : FileInfoId) : F[FileInfo]

  def retrieveUserMeta(ownerId : FileInfoId) : F[List[(FileInfoId, FileInfo)]]

  def updateFileSaveTime(fileId : FileInfoId) : F[Unit]

  def isOwner(fileId : FileInfoId, ownerId : UserId)(implicit F : Functor[F]) : F[Boolean] = retrieveMeta(fileId).map{
    fi => fi.uploadedBy.compareTo(ownerId) == 0
  }
}
