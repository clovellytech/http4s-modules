package h4sm
package files.client

import files.domain.{Backend, FileInfo}
import files.infrastructure.endpoint._
import files.Unshow
import io.circe.Decoder
import org.http4s.EntityDecoder
import cats.effect.Sync
import io.circe.generic.semiauto.deriveDecoder
import io.circe.Decoder
import org.http4s.circe.jsonOf
import org.http4s.EntityDecoder

class FileCodecs[F[_]: Sync] {
  implicit def backendDecoder[A <: Backend](implicit U: Unshow[A]): Decoder[Backend] =
    Decoder[String].map(U.unshow(_))
  implicit val backendDec: EntityDecoder[F, Backend] = jsonOf

  implicit val fileInfoDecoder: Decoder[FileInfo] = deriveDecoder
  implicit val fileInfoDec: EntityDecoder[F, FileInfo] = jsonOf

  implicit def fileListDecoder[A: Decoder]: Decoder[SiteResult[A]] = deriveDecoder
  implicit def fileListDec[A: Decoder]: EntityDecoder[F, SiteResult[A]] = jsonOf
}
