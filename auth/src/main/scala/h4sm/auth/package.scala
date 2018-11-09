package h4sm

import java.util.UUID

import cats.Monad
import h4sm.auth.db.domain.User
import org.http4s.Response
import tsec.authentication.{SecuredRequest, TSecAuthService, TSecBearerToken}

package object auth {

  type Instant = java.time.Instant
  type UserId = UUID

  type SecureRandomId = tsec.common.SecureRandomId

  type BearerToken = TSecBearerToken[UserId]
  val BearerToken = TSecBearerToken

  type BearerAuthService[F[_]] = TSecAuthService[User, BearerToken, F]

  def BearerAuthService[M[_] : Monad](
    pf: PartialFunction[SecuredRequest[M, User, BearerToken], M[Response[M]]]
  ) : BearerAuthService[M] = TSecAuthService(pf)
}
