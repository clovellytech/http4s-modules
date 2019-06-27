package h4sm.invitations
package infrastructure.endpoint

import cats.effect.Sync
import domain.Invitation
import io.circe._
import io.circe.generic.semiauto._
import org.http4s.circe._
import org.http4s.{EntityEncoder, EntityDecoder}

class Codecs[F[_]: Sync] {
  implicit val invitationRequestEnc: Encoder[InvitationRequest] = deriveEncoder
  implicit val invitationRequestEnc2: EntityEncoder[F, InvitationRequest] = jsonEncoderOf
  implicit val invitationRequestDec: Decoder[InvitationRequest] = deriveDecoder
  implicit val invitationRequestDec2: EntityDecoder[F, InvitationRequest] = jsonOf
  
  implicit val inviteSignupEnc: Encoder[InviteSignup] = deriveEncoder
  implicit val inviteSignupEnc2: EntityEncoder[F, InviteSignup] = jsonEncoderOf
  implicit val inviteSignupDec: Decoder[InviteSignup] = deriveDecoder
  implicit val inviteSignupDec2: EntityDecoder[F, InviteSignup] = jsonOf

  implicit val invitationByCodeReqDec: Decoder[InvitationByCodeRequest] = deriveDecoder
  implicit val invitationByCodeReqDec2: EntityDecoder[F, InvitationByCodeRequest] = jsonOf

  implicit def invitationDec[A: Decoder]: Decoder[Invitation[A]] = deriveDecoder
  implicit def invitationDec2[A: Decoder]: EntityDecoder[F, Invitation[A]] = jsonOf
}
