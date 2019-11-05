package h4sm.petstore
package infrastructure.endpoint

import domain._
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._

object codecs {
  implicit val petReqEnc: Encoder[PetRequest] = deriveEncoder
  implicit val petReqDec: Decoder[PetRequest] = deriveDecoder

  implicit val petEnc: Encoder[Pet] = deriveEncoder
  
  implicit val orderRequestDec: Decoder[OrderRequest] = deriveDecoder  
  implicit val orderRequestEnc: Encoder[OrderRequest] = deriveEncoder
}
