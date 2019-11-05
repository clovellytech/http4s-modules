package h4sm.auth
package comm

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

trait UserRequestCodecs {
  implicit val userReqDecoder: Decoder[UserRequest] = deriveDecoder
  implicit val userReqEncoder: Encoder[UserRequest] = deriveEncoder

  implicit def siteResultEnc[A: Encoder]: Encoder[SiteResult[A]] = deriveEncoder
  implicit def siteResultDec[A: Decoder]: Decoder[SiteResult[A]] = deriveDecoder
}
