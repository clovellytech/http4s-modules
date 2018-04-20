package com.clovellytech.auth.infrastructure

import cats.effect.Sync
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import io.circe.java8.time._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

package object endpoint {
  implicit val userReqDecoder : Decoder[UserRequest] = deriveDecoder[UserRequest]
  implicit def userReqEntityDecoder[F[_] : Sync] : EntityDecoder[F, UserRequest] = jsonOf
  implicit val userReqEncoder : Encoder[UserRequest] = deriveEncoder
  implicit def userReqEntityEncoder[F[_]: Sync] : EntityEncoder[F, UserRequest] = jsonEncoderOf

  implicit val userDetailDecoder : Decoder[UserDetail] = deriveDecoder
  implicit def userDetailEntityDec[F[_] : Sync] : EntityDecoder[F, UserDetail] = jsonOf
  implicit val userDetailEncoder : Encoder[UserDetail] = deriveEncoder
  implicit def userDetailEntityEnc[F[_]: Sync] : EntityEncoder[F, UserDetail] = jsonEncoderOf
}
