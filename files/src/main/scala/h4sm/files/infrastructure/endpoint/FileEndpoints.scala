package h4sm.files
package infrastructure.endpoint

import java.io.File
import java.util.UUID

import h4sm.files.domain._
import cats.implicits._
import org.http4s._
import org.http4s.multipart._
import cats.effect.{ContextShift, Sync}
import h4sm.auth.BearerAuthService
import h4sm.auth.infrastructure.endpoint.AuthEndpoints
import h4sm.files.domain.FileMetaAlgebra
import org.http4s.dsl._
import tsec.authentication._
import config._
import doobie.util.transactor.Transactor
import h4sm.files.db.FileInfoId
import org.http4s.headers.Location
import fs2.Stream
import h4sm.files.infrastructure.backends.{FileMetaService, LocalFileStoreService}

import scala.concurrent.ExecutionContext

class FileEndpoints[F[_]](auth : AuthEndpoints[F, _])(implicit
  S  : Sync[F],
  F  : FileMetaAlgebra[F],
  FS : FileStoreAlgebra[F],
  CS : ContextShift[F],
  C  : ConfigAsk[F],
  ec : ExecutionContext
) extends Http4sDsl[F] {

  val fileNotExists : F[File] = Error.fileNotExistError("Requested file not found").raiseError[F, File]

  def getFile(uuid: String, allowPred : FileInfo => Boolean) : F[(FileInfo, FileInfoId, Stream[F, Byte])] = for {
    uuid <- S.delay(UUID.fromString(uuid))
    conf <- C.ask
    baseFile = new File(conf.basePath)
    fileInfo <- F.retrieveMeta(uuid)
    _ <- if(allowPred(fileInfo)) (new File(baseFile, uuid.toString)).pure[F] else fileNotExists
  } yield (fileInfo, uuid, FS.retrieve(uuid))

  def unAuthEndpoints: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "request" / uuidstr => for {
      fileInfo <- getFile(uuidstr, _.isPublic)
      (meta, _, bytes) = fileInfo
      // need to figure out content type and filename parts.
      resp <- Ok(bytes)
    } yield resp

    case GET -> Root / "config" => C.ask.flatMap(c => Ok(c.uploadMax.toString))
  }

  def authEndpoints: BearerAuthService[F] = BearerAuthService {
    case req@GET -> Root / uuidstr asAuthed _ => {
      val pred = (i: FileInfo) => i.isPublic || (i.uploadedBy.compareTo(req.authenticator.identity) == 0)
      for {
        fileInfo <- getFile(uuidstr, pred)
        (_, _, bytes) = fileInfo
        resp <- Ok(bytes)
      } yield resp
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
              _ <- FS.write(finfoId, finfo, part.body)
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
      _ <- F.retrieveMeta(uuid)
      file <- FS.retrieveFile(uuid)
      resp <- StaticFile.fromFile[F](file, ec, req.request.some).getOrElseF(BadRequest())
    } yield resp
  }

  def endpoints : HttpRoutes[F] = unAuthEndpoints.combineK(auth.Auth.liftService(authEndpoints))
}

object FileEndpoints {
  def persistingEndpoints[F[_] : Sync : ContextShift : ConfigAsk](
    xa : Transactor[F],
    authEndpoints: AuthEndpoints[F, _],
    ec : ExecutionContext
  ) : FileEndpoints[F] = {
    implicit val fileMeta = new FileMetaService[F](xa)
    implicit val ex = ec
    implicit val fileStore = new LocalFileStoreService[F]()
    new FileEndpoints[F](authEndpoints)
  }
}
