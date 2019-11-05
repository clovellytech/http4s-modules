package h4sm.permissions.infrastructure.endpoint

import h4sm.permissions.domain.Permission
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

object codecs {
  implicit val permissionsEncoder: Encoder[Permission] = deriveEncoder
  implicit val permissionsDecoder: Decoder[Permission] = deriveDecoder
}
