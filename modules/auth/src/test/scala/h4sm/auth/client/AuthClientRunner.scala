package h4sm
package auth.client

import auth.domain._
import auth.domain.users._
import auth.domain.tokens._
import auth.infrastructure.endpoint.Authenticators
import auth.infrastructure.repository.persistent._
import cats.effect.{Bracket, Sync}
import doobie.Transactor
import tsec.authentication.TSecBearerToken
import tsec.passwordhashers.PasswordHasher
import tsec.passwordhashers.jca.BCrypt


abstract class AuthClientRunner[F[_]: Sync: Bracket[?[_], Throwable]] {
  def xa: Transactor[F]
  
  implicit lazy val userAlg: UserRepositoryAlgebra[F] = new UserRepositoryInterpreter[F](xa)
  def userService(implicit P: PasswordHasher[F, BCrypt]) = new UserService[F, BCrypt](BCrypt)
  implicit lazy val tokenAlg: TokenRepositoryAlgebra[F] = new TokenRepositoryInterpreter[F](xa)
  def auth = Authenticators.bearer[F]
  def authClient(implicit P: PasswordHasher[F, BCrypt]) = new AuthClient[F, BCrypt, TSecBearerToken](userService, auth)
  def testAuthClient(implicit P: PasswordHasher[F, BCrypt]): TestAuthClient[F, BCrypt, TSecBearerToken] = new TestAuthClient(authClient)
}
