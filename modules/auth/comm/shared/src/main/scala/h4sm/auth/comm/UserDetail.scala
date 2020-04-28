package h4sm.auth.comm

import authIdTypes._

final case class UserDetail(
    username: String,
    joinTime: Instant,
)

final case class UserDetailId(
    username: String,
    joinTime: Instant,
    userId: UserId,
)
