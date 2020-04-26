package h4sm.messages

import java.util.UUID
import cats.ApplicativeError

package object domain {

  type MessageId = UUID
  object MessageId {
    def fromString[F[_]](name: String)(implicit F: ApplicativeError[F, Throwable]): F[UUID] =
      F.catchNonFatal(UUID.fromString(name))
  }
}
