package h4sm.auth.comm

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

trait UserDetailCodecs {
  implicit val userDetailDecoder: Decoder[UserDetail] = deriveDecoder
  implicit val userDetailEncoder: Encoder[UserDetail] = deriveEncoder
}
