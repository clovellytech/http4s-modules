package h4sm.invitations
package domain

import cats.data.OptionT
import h4sm.db._
import h4sm.auth.UserId
import java.time.Instant
import simulacrum.typeclass

@typeclass
trait InvitationAlgebra[F[_]] extends CRDAlgebra[F, InvitationId, Invitation[UserId], Instant]{
  def fromToEmail(toEmail: String): OptionT[F, Annotated]
  def fromCode(toEmail: String, code: String): OptionT[F, Annotated]
  def opened(invitationId: InvitationId): F[Unit]
}
