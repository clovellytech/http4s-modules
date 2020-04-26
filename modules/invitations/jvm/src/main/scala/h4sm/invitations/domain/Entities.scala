package h4sm.invitations
package domain

import java.time.Instant

final case class Invitation[A](
    fromUser: A,
    toName: String,
    toEmail: String,
    code: String,
    sendDate: Option[Instant],
    openDate: Option[Instant],
    acceptDate: Option[Instant],
    rejectDate: Option[Instant],
)

object Invitation {
  def apply[A](fromUser: A, toName: String, toEmail: String): Invitation[A] = Invitation(
    fromUser,
    toName,
    toEmail,
    ShortCode.generate(),
    None,
    None,
    None,
    None,
  )
}
