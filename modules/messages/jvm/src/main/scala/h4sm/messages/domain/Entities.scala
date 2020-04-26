package h4sm.messages.domain

import h4sm.auth.UserId
import java.time.Instant

final case class UserMessage(
    from: UserId,
    to: UserId,
    text: String,
    openTime: Option[Instant],
)
