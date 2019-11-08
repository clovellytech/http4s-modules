package h4sm.invitations
package domain

import java.time.Instant

final case class Invitation[A](
    fromUser: A,
    toName: String,
    toEmail: String,
    code: String,
    sendDate: Option[Instant] = None,
    openDate: Option[Instant] = None,
    acceptDate: Option[Instant] = None,
    rejectDate: Option[Instant] = None,
)

object Invitation {
  def apply[A](fromUser: A, toName: String, toEmail: String): Invitation[A] = Invitation(
    fromUser,
    toName,
    toEmail,
    ShortCode.generate(),
  )
}
