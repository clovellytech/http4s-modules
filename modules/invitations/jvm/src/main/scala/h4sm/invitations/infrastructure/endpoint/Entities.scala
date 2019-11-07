package h4sm.invitations.infrastructure.endpoint

final case class InvitationRequest(
    toName: String,
    toEmail: String,
)

final case class InviteSignup(
    email: String,
    password: String,
    name: String,
    inviteCode: String,
)

final case class InvitationByCodeRequest(
    toEmail: String,
    code: String,
)

sealed abstract class InvitationError extends Throwable with Product with Serializable {
  def message: String
}
object InvitationError {
  case object InvalidInvitation extends InvitationError {
    def message: String = "InvalidInvitation"
  }

  def invalid: InvitationError = InvalidInvitation
}
