package h4sm.petstore
package infrastructure.endpoint

import cats.effect.Sync
import domain._
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._
import java.time.Instant
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder}

trait Codecs[F[_]] {
  implicit val petReqEnc: Encoder[PetRequest] = deriveEncoder
  implicit def petReqEnoder(implicit F: Sync[F]): EntityEncoder[F, PetRequest] = jsonEncoderOf

  implicit val petReqDec: Decoder[PetRequest] = deriveDecoder
  implicit def petReqDecoder(implicit F: Sync[F]): EntityDecoder[F, PetRequest] = jsonOf

  implicit val petEnc: Encoder[Pet] = deriveEncoder
  implicit def petEncoder(implicit F: Sync[F]): EntityEncoder[F, Pet] = jsonEncoderOf

  implicit def instanceList(implicit F: Sync[F]): EntityEncoder[F, List[(Pet, PetId, Instant)]] = jsonEncoderOf

  implicit val orderRequestDec: Decoder[OrderRequest] = deriveDecoder
  implicit def orderRequestDecoder(implicit F: Sync[F]): EntityDecoder[F, OrderRequest] = jsonOf

  implicit val orderRequestEnc: Encoder[OrderRequest] = deriveEncoder
  implicit def orderRequestEncoder(implicit F: Sync[F]): EntityEncoder[F, OrderRequest] = jsonEncoderOf
}
