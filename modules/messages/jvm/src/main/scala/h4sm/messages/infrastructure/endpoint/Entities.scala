package h4sm.messages.infrastructure.endpoint

import h4sm.auth.UserId

final case class CreateMessageRequest(
    to: UserId,
    content: String,
)
