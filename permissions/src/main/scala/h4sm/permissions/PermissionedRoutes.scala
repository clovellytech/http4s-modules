package h4sm.permissions

import cats.Monad
import cats.data.{Kleisli, OptionT}
import cats.implicits._
import h4sm.permissions.domain.PermissionAlgebra
import org.http4s._
import tsec.authentication.{SecuredRequest, TSecAuthService}



object PermissionedRoutes {

  type PermissionedRoutes[F[_], I, A] = Kleisli[OptionT[F, ?], SecuredRequest[F, I, A], Response[F]]

  def apply[F[_] : PermissionAlgebra : Monad, I, A](perm : (String, String))(
    pf : PartialFunction[SecuredRequest[F, I, A], F[Response[F]]]
  ) : TSecAuthService[I, A, F] = {
    val P = implicitly[PermissionAlgebra[F]]

    def hasPermission : F[Boolean] = {
      (P.selectByAttributes _).tupled(perm).isDefined
    }

    val next1: PartialFunction[SecuredRequest[F, I, A], OptionT[F, Response[F]]] = pf.andThen(resp => OptionT.liftF(resp))
    val next2: SecuredRequest[F, I, A] => OptionT[F, Response[F]] = (req : SecuredRequest[F, I, A]) => next1.applyOrElse(req, Function.const(OptionT.none[F, Response[F]]))

    Kleisli((req : SecuredRequest[F, I, A]) =>
      OptionT.liftF(hasPermission).filter(identity) *> next2(req)
    )
  }

  def apply[I, A, F[_]](
    pf: PartialFunction[SecuredRequest[F, I, A], F[Response[F]]]
  )(implicit F: Monad[F]): TSecAuthService[I, A, F] =
    Kleisli(req => pf.andThen(OptionT.liftF(_)).applyOrElse(req, Function.const(OptionT.none)))
}
