package h4sm.auth.comm

trait AuthTypes {
  type Instant = java.time.Instant
  type UserId = java.util.UUID
}

object authIdTypes extends AuthTypes