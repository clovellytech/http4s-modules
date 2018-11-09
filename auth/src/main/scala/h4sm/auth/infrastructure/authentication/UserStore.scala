package h4sm.auth
package infrastructure
package authentication

import java.util.UUID

import cats.Monad
import cats.data.OptionT
import cats.implicits._
import tsec.common.SecureRandomId

import h4sm.auth.domain.tokens.TokenRepositoryAlgebra
import h4sm.auth.domain.users.UserRepositoryAlgebra
import tsec.authentication._
import db.domain._

trait FunctionKK[X[_[_]], Y[_[_]]] {
  def apply[M[_]: Monad](xa: X[M]) : Y[M]
}

object FunctionKK{
  type ~~>[X[_[_]], Y[_[_]]] = FunctionKK[X, Y]
}

import FunctionKK._

trait UserBackingStore[F[_]] extends BackingStore[F, UUID, User]
trait TokenBackingStore[F[_]] extends BackingStore[F, SecureRandomId, BearerToken]

object TransBackingStore {
  val tokenTrans = new (TokenRepositoryAlgebra ~~> TokenBackingStore){
    def apply[M[_] : Monad](xa: TokenRepositoryAlgebra[M]) : TokenBackingStore[M] =
      new TokenBackingStore[M] {
        override def put(elem: BearerToken): M[BearerToken] = xa.insert(elem).as(elem)
        override def update(v: BearerToken): M[BearerToken] = xa.update(v).as(v)
        override def delete(id: SecureRandomId): M[Unit] = xa.delete(id)
        override def get(id: SecureRandomId): OptionT[M, BearerToken] = xa.byId(id).map(_._1)
      }
  }

  val userTrans = new (UserRepositoryAlgebra ~~> UserBackingStore) {
    def apply[M[_]: Monad](xa: UserRepositoryAlgebra[M]): UserBackingStore[M] =
      new UserBackingStore[M] {
        def put(elem: User): M[User] = xa.insert(elem).as(elem)
        def get(id: UUID): OptionT[M, User] = xa.byId(id).map(_._1)
        def update(v: User): M[User] = xa.update(v).as(v)
        def delete(id: UUID): M[Unit] = xa.delete(id)
      }
  }
}
