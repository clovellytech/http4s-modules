package h4sm.permissions.infrastructure.repository

import cats.effect.Bracket
import cats.implicits._
import cats.data.OptionT
import doobie._
import doobie.implicits._
import h4sm.permissions.domain.{Permission, PermissionId, PermissionAlgebra}
import persistent.sql.permissions

class PermissionRepository[F[_]: Bracket[?[_], Throwable]](xa: Transactor[F]) extends PermissionAlgebra[F] {
  def insert(a: Permission): F[Unit] = permissions.insert(a).run.as(()).transact(xa)

  def insertGetId(a: Permission): OptionT[F, PermissionId] = OptionT.liftF(permissions.insertGetId(a).transact(xa))

  def delete(i: PermissionId): F[Unit] = permissions.delete(i).run.as(()).transact(xa)

  def select: F[List[(Permission, PermissionId, Unit)]] =
    permissions.select.to[List].map(_.map { case (p, pid) => (p, pid, ())}).transact(xa)

  def byId(id: PermissionId): OptionT[F, (Permission, PermissionId, Unit)] =
    OptionT(permissions.byId(id).option.transact(xa)).map((_, id, ()))

  def safeUpdate(id: PermissionId, u: Permission): F[Unit] = permissions.safeUpdate(id, u).run.as(()).transact(xa)

  def update(u: Permission): F[Unit] = (for {
    v <- permissions.byAttributes(u.appName, u.name).option
    _ <- v.fold(permissions.insert(u).run.as(())){ case (_, pid) => permissions.safeUpdate(pid, u).run.as(()) }
  } yield ()).transact(xa)

  def selectByAppName(appName: String): F[List[(Permission, PermissionId)]] =
    permissions.byAppName(appName).to[List].transact(xa)

  def selectByAttributes(appName: String, name: String): OptionT[F, (Permission, PermissionId)] =
    OptionT(permissions.byAttributes(appName: String, name: String).option.transact(xa))
}
