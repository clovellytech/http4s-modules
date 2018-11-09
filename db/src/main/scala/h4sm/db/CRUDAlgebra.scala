package h4sm.db

import cats.data.OptionT

trait Algebra[F[_], A]

trait CAlgebra[F[_], A] extends Algebra[F, A]{
  def insert(a: A) : F[Unit]
}

trait RAlgebra[F[_], I, A, AA] extends Algebra[F, A] {
  type Annotated = (A, I, AA)
  def select : F[List[Annotated]]
  def byId(id: I) : OptionT[F, Annotated]
}

trait UAlgebra[F[_], I, A] extends Algebra[F, A] {
  def safeUpdate(id: I, u: A): F[Unit]
  def update(u: A) : F[Unit]
}

trait DAlgebra[F[_], I, A] extends Algebra[F, A] {
  def delete(i: I) : F[Unit]
}

trait CRAlgebra[F[_], I, A, AA] extends CAlgebra[F, A] with RAlgebra[F, I, A, AA]

trait CRUAlgebra[F[_], I, A, AA] extends CRAlgebra[F, I, A, AA] with UAlgebra[F, I, A]

trait CRUDAlgebra[F[_], I, A, AA] extends CRUAlgebra[F, I, A, AA] with DAlgebra[F, I, A]
