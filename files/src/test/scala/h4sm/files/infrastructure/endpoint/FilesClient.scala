package h4sm.files
package infrastructure.endpoint

import java.io.File
import java.util.UUID

import cats.implicits._
import cats.effect.Sync
import h4sm.files.db.FileInfoId
import fs2.Stream
import h4sm.files.domain.FileInfo
import org.http4s._
import org.http4s.MediaType._
import org.http4s.headers._
import org.http4s.dsl._
import org.http4s.client.dsl._
import org.http4s.multipart._


sealed abstract class ClientError extends Throwable with Product with Serializable
final case class UriError(message : String) extends ClientError
final case class CommunicationError(status: Status, message : String) extends ClientError

class FilesClient[F[_]](fileEndpoints : FileEndpoints[F])(implicit F : Sync[F]) extends Http4sDsl[F] with Http4sClientDsl[F]{
  val codecs = new FileCodecs[F]
  import codecs._

  val files = fileEndpoints.endpoints.orNotFound
  def commError[A](status : Status) : Throwable = CommunicationError(status, "Error, status was not Ok")

  def passOk[A](response : Response[F]) : F[Response[F]] = response.status match {
    case Status.Ok => response.pure[F]
    case _ => commError(response.status).raiseError[F, Response[F]]
  }

  def postFile(fileInfo : FileInfo, file: File)(implicit h : Headers) : F[SiteResult[List[FileInfoId]]] = {
    val mp : Multipart[F] = Multipart(
      Vector(
        Part.fileData(fileInfo.name.getOrElse("file"), file, `Content-Type`(`text/plain`))
      )
    )
    for {
      req <- POST.apply(uri("/"), mp, h.toSeq ++ mp.headers.toSeq : _*)
      resp <- files.run(req)
      _ <- passOk(resp)
      fileRes <- resp.as[SiteResult[List[FileInfoId]]]
    } yield fileRes
  }

  def getFile(uuid : UUID, name : String = "download")(implicit h : Headers) : F[Stream[F, Byte]] = for {
    u <- Uri.fromString(s"/${uuid.toString}/$name").leftMap(_.asInstanceOf[Throwable]).raiseOrPure[F]
    req <- GET(u)
    resp <- files.run(req.withHeaders(h))
    _ <- passOk(resp)
  } yield resp.body

  def listFiles()(implicit h : Headers) : F[SiteResult[List[(FileInfoId, FileInfo)]]] = for {
    req <- GET(uri("/"))
    hreq = req.withHeaders(h)
    resp <- files.run(hreq)
//    _ <- passOk(resp)
    fileInfo <- resp.as[SiteResult[List[(FileInfoId, FileInfo)]]]
  } yield fileInfo
}
