package h4sm

import cats.Monad
import h4sm.auth.db.domain.User
import org.http4s.Response
import tsec.authentication._

package object auth {
  type Instant = java.time.Instant
  type UserId = java.util.UUID

  type SecureRandomId = tsec.common.SecureRandomId

  type UserSecuredRequest[F[_], T[_]] = SecuredRequest[F, User, T[UserId]]

  type UserAuthService[F[_], T[_]] = TSecAuthService[User, T[UserId], F]
  type UserAuthenticator[F[_], T[_]] = Authenticator[F, UserId, User, T[UserId]]

  type UserSecuredRequestHandler[F[_], T[_]] = SecuredRequestHandler[F, UserId, User, T[UserId]]

  type BearerAuthService[F[_]] = UserAuthService[F, TSecBearerToken]
  type BearerSecuredRequest[F[_]] = UserSecuredRequest[F, TSecBearerToken]

  def BearerAuthService[M[_]: Monad](
      pf: PartialFunction[UserSecuredRequest[M, TSecBearerToken], M[Response[M]]],
  ) =
    UserAuthService[M, TSecBearerToken](pf)

  def UserAuthService[M[_]: Monad, T[_]](
      pf: PartialFunction[UserSecuredRequest[M, T], M[Response[M]]],
  ): UserAuthService[M, T] = TSecAuthService(pf)
}
