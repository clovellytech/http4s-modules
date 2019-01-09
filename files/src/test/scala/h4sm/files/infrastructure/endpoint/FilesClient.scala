package h4sm
package files
package infrastructure.endpoint

import java.io.File
import java.util.UUID

import cats.implicits._
import cats.effect.{ContextShift, Sync}
import h4sm.files.db.FileInfoId
import fs2.Stream
import h4sm.files.domain.FileInfo
import org.http4s._
import org.http4s.implicits._
import org.http4s.headers._
import org.http4s.dsl._
import org.http4s.client.dsl._
import org.http4s.multipart._

import scala.concurrent.ExecutionContext

import dbtesting.endpoints.ClientError._

class FilesClient[F[_] : ContextShift](fileEndpoints : FileEndpoints[F])(implicit F : Sync[F], ec : ExecutionContext) extends Http4sDsl[F] with Http4sClientDsl[F]{
  val codecs = new FileCodecs[F]
  import codecs._

  val files = fileEndpoints.endpoints.orNotFound

  def postFile(fileInfo : FileInfo, file: File)(implicit h : Headers) : F[SiteResult[List[FileInfoId]]] = {
    val mp : Multipart[F] = Multipart(
      Vector(
        Part.fileData(fileInfo.name.getOrElse("file"), file, ec, `Content-Type`(MediaType.text.plain))
      )
    )
    for {
      req <- POST(mp, Uri.uri("/"), h.toSeq ++ mp.headers.toSeq : _*)
      resp <- files.run(req)
      _ <- passOk(resp)
      fileRes <- resp.as[SiteResult[List[FileInfoId]]]
    } yield fileRes
  }

  def getFile(uuid : UUID, name : String = "download")(implicit h : Headers) : F[Stream[F, Byte]] = for {
    u <- Uri.fromString(s"/${uuid.toString}/$name").leftWiden[Throwable].raiseOrPure[F]
    req <- GET(u)
    resp <- files.run(req.withHeaders(h))
    _ <- passOk(resp)
  } yield resp.body

  def listFiles()(implicit h : Headers) : F[SiteResult[List[(FileInfoId, FileInfo)]]] = for {
    req <- GET(Uri.uri("/"))
    hreq = req.withHeaders(h)
    resp <- files.run(hreq)
//    _ <- passOk(resp)
    fileInfo <- resp.as[SiteResult[List[(FileInfoId, FileInfo)]]]
  } yield fileInfo
}
