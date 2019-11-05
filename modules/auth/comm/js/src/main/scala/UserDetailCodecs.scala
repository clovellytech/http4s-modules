package h4sm.auth.comm

import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto.{deriveEncoder, deriveDecoder}

trait UserDetailCodecs {
  implicit val userDetailDecoder: Decoder[UserDetail] = deriveDecoder
  implicit val userDetailEncoder: Encoder[UserDetail] = deriveEncoder
}
