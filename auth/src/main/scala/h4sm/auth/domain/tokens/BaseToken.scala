package h4sm.auth.domain.tokens

import h4sm.auth.{Instant, UserId}
import simulacrum.{op, typeclass}
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

final case class BaseToken(secureId: SecureRandomId, identity: UserId, expiry: Instant, lastTouched: Option[Instant])

@typeclass
trait AsBaseToken[T] {
  @op("asBase") def asBase(t: T): BaseToken
}

@typeclass
trait BaseTokenReader[T]{
  @op("readToken") def read(b: BaseToken): T
}

object AsBaseTokenInstances{
  implicit val bearerAsBase: AsBaseToken[TSecBearerToken[UserId]] = new AsBaseToken[TSecBearerToken[UserId]] {
    def asBase(t: TSecBearerToken[UserId]): BaseToken = BaseToken(t.id, t.identity, t.expiry, t.lastTouched)
  }
}

object BaseTokenReaderInstances {
  implicit val asBearer: BaseTokenReader[TSecBearerToken[UserId]] = new BaseTokenReader[TSecBearerToken[UserId]]{
    def read(b: BaseToken): TSecBearerToken[UserId] = TSecBearerToken(
      b.secureId,
      b.identity,
      b.expiry,
      b.lastTouched
    )
  }
}