package h4sm.store
package infrastructure.endpoint

import domain._
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

object codecs {
  implicit val itemReqEnc: Encoder[ItemRequest] = deriveEncoder
  implicit val itemReqDec: Decoder[ItemRequest] = deriveDecoder
  implicit val itemEnc: Encoder[Item] = deriveEncoder
  implicit val orderRequestDec: Decoder[OrderRequest] = deriveDecoder
  implicit val orderRequestEnc: Encoder[OrderRequest] = deriveEncoder
  implicit val viewOrderRequestDec: Decoder[ViewOrderRequest] = deriveDecoder
}
