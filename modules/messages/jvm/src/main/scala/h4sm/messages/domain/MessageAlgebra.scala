package h4sm.messages.domain

import fs2.Stream
import h4sm.auth._
import h4sm.db.CRDAlgebra
import simulacrum.typeclass

@typeclass
trait MessageAlgebra[F[_]] extends CRDAlgebra[F, MessageId, UserMessage, Instant] {
  def inbox(userId: UserId): Stream[F, Annotated]
  def thread(from: UserId, to: UserId): Stream[F, Annotated]
}
