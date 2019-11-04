package h4sm.auth.comm

import java.time.Instant

final case class UserDetail(
  username: String,
  joinTime: Instant,
)
