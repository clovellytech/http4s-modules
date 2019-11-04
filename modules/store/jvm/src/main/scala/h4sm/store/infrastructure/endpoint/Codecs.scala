package h4sm.store
package infrastructure.endpoint

import cats.Applicative
import cats.effect.Sync
import domain._
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder}

trait Codecs[F[_]] {
  implicit val itemReqEnc: Encoder[ItemRequest] = deriveEncoder
  implicit val itemReqDec: Decoder[ItemRequest] = deriveDecoder
  implicit val itemEnc: Encoder[Item] = deriveEncoder
  implicit val orderRequestDec: Decoder[OrderRequest] = deriveDecoder
  implicit val orderRequestEnc: Encoder[OrderRequest] = deriveEncoder
  implicit val viewOrderRequestDec: Decoder[ViewOrderRequest] = deriveDecoder

  implicit def encEnc[A: Encoder](implicit F: Applicative[F]): EntityEncoder[F, A] = jsonEncoderOf[F, A]
  implicit def decDec[A: Decoder](implicit F: Sync[F]): EntityDecoder[F, A] = jsonOf[F, A]
}
