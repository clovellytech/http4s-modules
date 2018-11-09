package com.clovellytech
package files
package infrastructure.endpoint

import java.io.File
import java.util.UUID

import com.clovellytech.files.domain._
import cats.implicits._
import org.http4s._
import org.http4s.multipart._
import cats.effect.Sync
import com.clovellytech.auth.BearerAuthService
import com.clovellytech.auth.infrastructure.endpoint.AuthEndpoints
import com.clovellytech.files.domain.FileMetaAlgebra
import org.http4s.dsl._
import tsec.authentication._
import config._
import org.http4s.headers.Location

class FileEndpoints[F[_]](auth : AuthEndpoints[F, _])(implicit
  S  : Sync[F],
  F  : FileMetaAlgebra[F],
  FS : FileStoreAlgebra[F],
  C  : ConfigAsk[F]
) extends Http4sDsl[F] {

  val fileNotExists : F[File] = Error.fileNotExistError("Requested file not found").raiseError[F, File]

  def getFile(uuid: String, allowPred : FileInfo => Boolean) : F[File] = for {
    uuid <- S.delay(UUID.fromString(uuid))
    conf <- C.ask
    baseFile = new File(conf.basePath)
    fileInfo <- F.retrieveMeta(uuid)
    file <- if(allowPred(fileInfo)) (new File(baseFile, uuid.toString)).pure[F] else fileNotExists
  } yield file

  def unAuthEndpoints: HttpService[F] = HttpService {
    case GET -> Root / "request" / uuidstr => getFile(uuidstr, _.isPublic).flatMap(Ok apply _)

    case GET -> Root / "config" => C.ask.flatMap(c => Ok(c.uploadMax.toString))
  }

  def authEndpoints: BearerAuthService[F] = BearerAuthService {
    case req@GET -> Root / uuidstr asAuthed _ => {
      val pred = (i: FileInfo) => i.isPublic || (i.uploadedBy.compareTo(req.authenticator.identity) == 0)
      getFile(uuidstr, pred).flatMap(Ok apply _)
    }

    case req@GET -> Root asAuthed _ => for {
      infos <- F.retrieveUserMeta(req.authenticator.identity)
      resp <- Ok(SiteResult(infos))
    } yield resp

    case req@POST -> Root asAuthed _ => {
      val decoded = EntityDecoder[F, Multipart[F]].decode(req.request, true)
      decoded.value.flatMap(_.fold(
        df => {
          println("Decode failure")
          println(df)
          BadRequest()
        },
        mp => for {
          savedFileIds <- mp.parts.traverse { part =>
            val finfo = FileInfo(part.name, none, part.filename, none, req.authenticator.identity, false)
            for {
              finfoId <- F.storeMeta(finfo)
              fileSave <- FS.write(finfoId, finfo, part.body)
            } yield finfoId
          }
          resp <- Ok(SiteResult(savedFileIds.toList))
        } yield resp
      ))
    }

    case POST -> Root / uuid asAuthed _ => for {
      uuid <- S.delay(UUID.fromString(uuid))
      fileInfo <- F.retrieveMeta(uuid)
      url <- Uri.fromString(s"/$uuid/${fileInfo.filename.getOrElse("download")}").leftMap(_.asInstanceOf[Throwable]).raiseOrPure[F]
      resp <- TemporaryRedirect(Location(url))
    } yield resp

    case req@GET -> Root / uuid / _ asAuthed _ => for {
      uuid <- S.delay(UUID.fromString(uuid))
      fileInfo <- F.retrieveMeta(uuid)
      file <- FS.retrieveFile(uuid)
      resp <- StaticFile.fromFile[F](file, req.request.some).getOrElseF(BadRequest())
    } yield resp
  }

  def endpoints : HttpService[F] = unAuthEndpoints.combineK(auth.Auth.liftService(authEndpoints))
}
