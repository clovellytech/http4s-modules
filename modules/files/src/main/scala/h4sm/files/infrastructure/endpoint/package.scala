package h4sm.files.infrastructure

import cats.effect.Sync
import h4sm.files.domain.{Backend, FileInfo}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}
import cats.syntax.show._

package object endpoint {
  implicit val fileUploadDecoder : Decoder[FileUpload] = deriveDecoder
  implicit def fileUploadDec[F[_] : Sync] : EntityDecoder[F, FileUpload] = jsonOf

  implicit val backendEncoder : Encoder[Backend] = Encoder[String].contramap(_.show)
  implicit def backendEnc[F[_] : Sync] : EntityEncoder[F, Backend] = jsonEncoderOf

  implicit val fileInfoEncoder : Encoder[FileInfo] = deriveEncoder
  implicit def fileInfoEnc[F[_] : Sync] : EntityEncoder[F, FileInfo] = jsonEncoderOf

  implicit def siteResultEncoder[A : Encoder] : Encoder[SiteResult[A]] = deriveEncoder
  implicit def siteResultEnc[F[_] : Sync, A : Encoder] : EntityEncoder[F, SiteResult[A]] = jsonEncoderOf
}
