package h4sm.auth.comm

final case class SiteResult[A](result: A)

final case class UserRequest(
  username: String,
  password: String,
)
