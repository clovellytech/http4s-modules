package h4sm.auth.domain.tokens

import h4sm.auth.{Instant, UserId}
import simulacrum.{op, typeclass}
import tsec.authentication._
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

trait AsBaseTokenInstances{
  implicit val bearerAsBase: AsBaseToken[TSecBearerToken[UserId]] = new AsBaseToken[TSecBearerToken[UserId]] {
    def asBase(t: TSecBearerToken[UserId]): BaseToken = BaseToken(t.id, t.identity, t.expiry, t.lastTouched)
  }
  implicit def encryptedCookieAsBase[A]: AsBaseToken[AuthEncryptedCookie[A, UserId]] = new AsBaseToken[AuthEncryptedCookie[A, UserId]] {
    def asBase(t: AuthEncryptedCookie[A, UserId]): BaseToken = BaseToken(SecureRandomId(t.id.toString), t.identity, t.expiry, t.lastTouched)
  }
}

trait BaseTokenReaderInstances {
  implicit val asBearer: BaseTokenReader[TSecBearerToken[UserId]] = new BaseTokenReader[TSecBearerToken[UserId]]{
    def read(b: BaseToken): TSecBearerToken[UserId] = TSecBearerToken(
      b.secureId,
      b.identity,
      b.expiry,
      b.lastTouched
    )
  }
}