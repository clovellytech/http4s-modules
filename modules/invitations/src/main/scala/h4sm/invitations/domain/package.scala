package h4sm.invitations

import cats.ApplicativeError
import java.util.UUID

package object domain {
  type InvitationId = UUID
  object InvitationId {
    def fromString[F[_]: ApplicativeError[?[_], Throwable]](name: String): F[InvitationId] = 
      ApplicativeError[F, Throwable].catchNonFatal(UUID.fromString(name))
  }
}
