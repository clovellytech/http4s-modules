package h4sm.files.infrastructure

import h4sm.files.domain.{Backend, FileInfo}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import cats.syntax.show._

package object endpoint {
  implicit val fileUploadDecoder: Decoder[FileUpload] = deriveDecoder

  implicit val backendEncoder: Encoder[Backend] = Encoder[String].contramap(_.show)

  implicit val fileInfoEncoder: Encoder[FileInfo] = deriveEncoder
}
