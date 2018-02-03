package com.clovellytech.featurerequests.infrastructure

import cats.effect.Sync
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._
import org.joda.time.DateTime

import com.clovellytech.featurerequests.db.domain.Feature
import com.clovellytech.featurerequests.domain.requests.FeatureRequest
import com.clovellytech.featurerequests.domain.votes.VoteRequest

package object endpoint {
  implicit val dateTimeEncoder: Encoder[DateTime] = Encoder.instance(a => a.getMillis.asJson)
  implicit val dateTimeDecoder: Decoder[DateTime] = Decoder.instance(a => a.as[Long].map(new DateTime(_)))

  implicit val voteReqDec : Decoder[VoteRequest] = deriveDecoder[VoteRequest]

  implicit val featureReqDecoder : Decoder[FeatureRequest] = deriveDecoder[FeatureRequest]
  implicit def featureReqEntityDecoder[F[_] : Sync] : EntityDecoder[F, FeatureRequest] = jsonOf
  implicit val featureReqEncoder : Encoder[FeatureRequest] = deriveEncoder
  implicit def featureReqEntityEncoder[F[_]: Sync] : EntityEncoder[F, FeatureRequest] = jsonEncoderOf

  implicit def voteRequestEntityDecoder[F[_] : Sync] : EntityDecoder[F, VoteRequest] = jsonOf

  implicit val featureEncoder : Encoder[Feature] = deriveEncoder
  implicit def featureEntityEncoder[F[_] : Sync] : EntityEncoder[F, Feature] = jsonEncoderOf

  implicit val featureDecoder : Decoder[Feature] = deriveDecoder
  implicit def featureEntityDecoder[F[_] : Sync] : EntityDecoder[F, Feature] = jsonOf

  implicit val votedFeatEncoder : Encoder[VotedFeatures] = deriveEncoder
  implicit def votedFeatEntityEncoder[F[_]: Sync] : EntityEncoder[F, VotedFeatures] = jsonEncoderOf

  implicit val votedFeatDecoder : Decoder[VotedFeatures] = deriveDecoder[VotedFeatures]
  implicit def votedFeatEntityDecoder[F[_]: Sync] : EntityDecoder[F, VotedFeatures] = jsonOf

  implicit def defaultResultEncoder[A: Encoder] : Encoder[DefaultResult[A]] = deriveEncoder
  implicit def defaultResultEntityEncoder[F[_]: Sync, A: Encoder] : EntityEncoder[F, DefaultResult[A]] = jsonEncoderOf[F, DefaultResult[A]]

  implicit def defaultResultDecoder[A: Decoder] : Decoder[DefaultResult[A]] = deriveDecoder[DefaultResult[A]]
  implicit def defaultResultEntityDecoder[F[_]: Sync, A: Decoder] : EntityDecoder[F, DefaultResult[A]] = jsonOf[F, DefaultResult[A]]
}
