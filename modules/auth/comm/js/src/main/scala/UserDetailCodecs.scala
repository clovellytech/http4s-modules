package h4sm.auth.comm

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

trait UserDetailCodecs {
  implicit val userDetailDecoder: Decoder[UserDetail] = deriveDecoder
  implicit val userDetailEncoder: Encoder[UserDetail] = deriveEncoder
}
