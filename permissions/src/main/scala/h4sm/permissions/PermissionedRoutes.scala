package h4sm.permissions

import cats.Monad
import cats.data.{Kleisli, OptionT}
import h4sm.auth._
import h4sm.auth.domain.tokens.AsBaseToken
import AsBaseToken.ops._
import h4sm.permissions.domain.UserPermissionAlgebra
import org.http4s._

/**
  * Route creator for routes blocked by user permissions for authenticated users.
  *
  * Example:
  *
  * PermissionedRoutes ("appname" -> "permissionName") {
  *   case req@GET@Root asAuthed user => ...
  * }
  */
object PermissionedRoutes {

  def apply[F[_] : UserPermissionAlgebra : Monad, T[_]](perm : (String, String))(
    pf : PartialFunction[UserSecuredRequest[F, T], F[Response[F]]]
  )(implicit a: AsBaseToken[T[UserId]]) : UserAuthService[F, T] = {
    def hasPermission(id : UserId) : F[Boolean] = UserPermissionAlgebra[F].hasPermission(id, perm._1, perm._2)

    Kleisli { (req: UserSecuredRequest[F, T]) =>
      for {
        _ <- OptionT.liftF(hasPermission(req.authenticator.asBase.identity)).filter(identity)
        resp <- pf.andThen(OptionT.liftF(_)).applyOrElse(req, Function.const(OptionT.none[F, Response[F]]))
      } yield resp
    }
  }

  def apply[F[_]: Monad, T[_]](pf: PartialFunction[UserSecuredRequest[F, T], F[Response[F]]]): UserAuthService[F, T] =
    Kleisli(req => pf.andThen(OptionT.liftF(_)).applyOrElse(req, Function.const(OptionT.none)))
}
