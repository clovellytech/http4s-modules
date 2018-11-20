package h4sm.db

import cats.data.OptionT

trait Service[F[_], A]{
  def algebra : Alg
  type Alg <: Algebra[F, A]
}

trait CService[F[_], I, A] extends Service[F, A]{
  type Alg <: CAlgebra[F, I, A]

  def insert(a: A): F[Unit] = algebra.insert(a)
  def insertGetId(a : A): OptionT[F, I] = algebra.insertGetId(a)
}

trait RService[F[_], I, A, AA] extends Service[F, A]{
  type Alg <: RAlgebra[F, I, A, AA]

  def select: F[List[(A, I, AA)]] = algebra.select
  def byId(id: I): OptionT[F, (A, I, AA)] = algebra.byId(id)
}

trait CRService[F[_], I, A, AA] extends CService[F, I, A] with RService[F, I, A, AA] {
  type Alg <: CRAlgebra[F, I, A, AA]
}

trait UService[F[_], I, A] extends Service[F, A] {
  type Alg <: UAlgebra[F, I, A]

  def update(id: I, a: A) : F[Unit] = algebra.safeUpdate(id, a)
}

trait CRUService[F[_], I, A, AA] extends CRService[F, I, A, AA] with UService[F, I, A] {
  type Alg <: CRUAlgebra[F, I, A, AA]
}

trait DService[F[_], I, A] extends Service[F, A] {
  type Alg <: DAlgebra[F, I, A]
  def delete(id: I) : F[Unit] = algebra.delete(id)
}

trait CRUDService[F[_], I, A, AA] extends CRUService[F, I, A, AA] with DService[F, I, A]{
  type Alg <: CRUDAlgebra[F, I, A, AA]
}
