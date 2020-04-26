package h4sm.messages.infrastructure.endpoint

import h4sm.messages.domain._
import io.circe.Codec
import io.circe.generic.semiauto._


object Codecs {
  implicit val messageEnc: Codec[UserMessage] = deriveCodec
  implicit val messageReqDec: Codec[CreateMessageRequest] = deriveCodec
}
