package h4sm.invitations
package infrastructure.endpoint

import domain.Invitation
import io.circe._
import io.circe.generic.semiauto._

object codecs {
  implicit val invitationRequestEnc: Encoder[InvitationRequest] = deriveEncoder
  implicit val invitationRequestDec: Decoder[InvitationRequest] = deriveDecoder
  
  implicit val inviteSignupEnc: Encoder[InviteSignup] = deriveEncoder
  implicit val inviteSignupDec: Decoder[InviteSignup] = deriveDecoder
  
  implicit val invitationByCodeReqDec: Decoder[InvitationByCodeRequest] = deriveDecoder
  
  implicit def invitationDec[A: Decoder]: Decoder[Invitation[A]] = deriveDecoder
}
