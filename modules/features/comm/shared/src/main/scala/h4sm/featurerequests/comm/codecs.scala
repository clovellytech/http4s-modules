package h4sm.featurerequests.comm

import java.time.Instant

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import h4sm.featurerequests.comm.domain.features.{Feature, FeatureRequest}
import h4sm.featurerequests.comm.domain.votes.{VoteRequest, VotedFeature}

object codecs {
  implicit val dateTimeEncoder: Encoder[Instant] = Encoder.instance(a => a.toEpochMilli.asJson)
  implicit val dateTimeDecoder: Decoder[Instant] =
    Decoder.instance(a => a.as[Long].map(Instant.ofEpochMilli(_)))

  implicit val featureReqDecoder: Decoder[FeatureRequest] = deriveDecoder[FeatureRequest]
  implicit val featureReqEncoder: Encoder[FeatureRequest] = deriveEncoder

  implicit val voteRequestDecoder: Decoder[VoteRequest] = deriveDecoder[VoteRequest]
  implicit val voteRequestEncoder: Encoder[VoteRequest] = deriveEncoder

  implicit val featureEncoder: Encoder[Feature] = deriveEncoder
  implicit val featureDecoder: Decoder[Feature] = deriveDecoder

  implicit val votedFeatEncoder: Encoder[VotedFeature] = deriveEncoder
  implicit val votedFeatDecoder: Decoder[VotedFeature] = deriveDecoder[VotedFeature]
}
