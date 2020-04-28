package h4sm.auth.comm

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

trait UserDetailCodecs {
  implicit val userDetailDecoder: Codec[UserDetail] = deriveCodec

  implicit val userDetailIdEncoder: Codec[UserDetailId] = deriveCodec
}
