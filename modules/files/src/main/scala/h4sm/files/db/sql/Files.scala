package h4sm.files
package db.sql

import domain._
import h4sm.auth.UserId
import h4sm.files.db._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

final case class InterFileInfo(
  name: Option[String],
  description: Option[String],
  filename : Option[String],
  uri: Option[String],
  uploadedBy: UserId,
  isPublic : Boolean,
  backend : Option[String]
){
  def toFileInfo(implicit unshow : Unshow[Backend]) : FileInfo =
    FileInfo(name, description, filename, uri, uploadedBy, isPublic, backend.map(unshow.unshow _).getOrElse(Backend.LocalBackend))
}

trait FilesSQL{
  def selectById(fileInfoId : FileInfoId) : Query0[FileInfo] = sql"""
    select f.name, f.description, f.user_filename, f.uri, f.uploaded_by, f.is_public, backend
    from ct_files.file_meta f
    where f.file_meta_id = $fileInfoId
  """.query[InterFileInfo].map(_.toFileInfo)

  def selectFiles(ownerId : UserId) : Query0[(FileInfoId, FileInfo)] = sql"""
    select f.file_meta_id, f.name, f.description, f.user_filename, f.uri, f.uploaded_by, f.is_public, backend
    from ct_files.file_meta f
    where f.uploaded_by = $ownerId
  """.query[(FileInfoId, InterFileInfo)].map{ case (id, info) => (id, info.toFileInfo) }

  def insert(fileInfo : FileInfo) : Update0 = {
    val FileInfo(name, description, filename, uri, uploadedBy, isPublic, backend) = fileInfo
    sql"""
      insert
      into ct_files.file_meta (name, description, user_filename, uri, uploaded_by, is_public, backend)
      values ($name, $description, $filename, $uri, $uploadedBy, $isPublic, $backend)
    """.update
  }

  def updateFileUploadTime(fileId : FileInfoId) : Update0 = sql"""
    update ct_files.file_meta
    set creation_time = now()
    where file_meta_id = $fileId
  """.update

  def insertGenId(fileInfo : FileInfo) : ConnectionIO[FileInfoId] =
    insert(fileInfo).withUniqueGeneratedKeys("file_meta_id")

  def deleteById(fileId : FileInfoId) : Update0 = sql"""
    delete from ct_files.file_meta
    where file_meta_id = $fileId
  """.update
}
