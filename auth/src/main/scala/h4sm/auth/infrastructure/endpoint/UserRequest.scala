package h4sm.auth.infrastructure.endpoint

import java.time.Instant

final case class UserRequest(
  username: String,
  password: Array[Byte]
)

final case class UserDetail(
  username: String,
  joinTime: Instant
)
