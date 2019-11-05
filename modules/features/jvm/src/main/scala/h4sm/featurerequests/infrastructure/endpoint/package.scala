package h4sm.featurerequests.infrastructure

import java.time.Instant

import cats.effect.Sync
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._
import h4sm.featurerequests.db.domain._
import h4sm.featurerequests.domain.requests.FeatureRequest
import h4sm.featurerequests.domain.votes.VoteRequest

package object endpoint {
  implicit val dateTimeEncoder: Encoder[Instant] = Encoder.instance(a => a.toEpochMilli.asJson)
  implicit val dateTimeDecoder: Decoder[Instant] = Decoder.instance(a => a.as[Long].map(Instant.ofEpochMilli(_)))

  implicit val featureReqDecoder: Decoder[FeatureRequest] = deriveDecoder[FeatureRequest]
  implicit def featureReqEntityDecoder[F[_]: Sync]: EntityDecoder[F, FeatureRequest] = jsonOf
  implicit val featureReqEncoder: Encoder[FeatureRequest] = deriveEncoder
  implicit def featureReqEntityEncoder[F[_]: Sync]: EntityEncoder[F, FeatureRequest] = jsonEncoderOf

  implicit val voteRequestDecoder: Decoder[VoteRequest] = deriveDecoder[VoteRequest]
  implicit def voteRequestEntityDecoder[F[_]: Sync]: EntityDecoder[F, VoteRequest] = jsonOf
  implicit val voteRequestEncoder: Encoder[VoteRequest] = deriveEncoder
  implicit def voteRequestEntityEncoder[F[_]: Sync]: EntityEncoder[F, VoteRequest] = jsonEncoderOf

  implicit val featureEncoder: Encoder[Feature] = deriveEncoder
  implicit def featureEntityEncoder[F[_]: Sync]: EntityEncoder[F, Feature] = jsonEncoderOf

  implicit val featureDecoder: Decoder[Feature] = deriveDecoder
  implicit def featureEntityDecoder[F[_]: Sync]: EntityDecoder[F, Feature] = jsonOf

  implicit val votedFeatEncoder: Encoder[VotedFeature] = deriveEncoder
  implicit def votedFeatEntityEncoder[F[_]: Sync]: EntityEncoder[F, VotedFeature] = jsonEncoderOf

  implicit val votedFeatDecoder: Decoder[VotedFeature] = deriveDecoder[VotedFeature]
  implicit def votedFeatEntityDecoder[F[_]: Sync]: EntityDecoder[F, VotedFeature] = jsonOf

  implicit def defaultResultEncoder[A: Encoder]: Encoder[DefaultResult[A]] = deriveEncoder
  implicit def defaultResultEntityEncoder[F[_]: Sync, A: Encoder]: EntityEncoder[F, DefaultResult[A]] = jsonEncoderOf[F, DefaultResult[A]]

  implicit def defaultResultDecoder[A: Decoder]: Decoder[DefaultResult[A]] = deriveDecoder[DefaultResult[A]]
  implicit def defaultResultEntityDecoder[F[_]: Sync, A: Decoder]: EntityDecoder[F, DefaultResult[A]] = jsonOf[F, DefaultResult[A]]
}
