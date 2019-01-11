package h4sm.permissions

import cats.Monad
import cats.data.{Kleisli, OptionT}
import h4sm.auth._
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

  def apply[F[_] : UserPermissionAlgebra : Monad](perm : (String, String))(
    pf : PartialFunction[BearerSecuredRequest[F], F[Response[F]]]
  ) : BearerAuthService[F] = {
    val P = implicitly[UserPermissionAlgebra[F]]

    def hasPermission(id : UserId) : F[Boolean] = P.hasPermission(id, perm._1, perm._2)

    Kleisli { (req: BearerSecuredRequest[F]) =>
      for {
        _ <- OptionT.liftF(hasPermission(req.authenticator.identity)).filter(identity)
        resp <- pf.andThen(OptionT.liftF(_)).applyOrElse(req, Function.const(OptionT.none[F, Response[F]]))
      } yield resp
    }
  }

  def apply[F[_]](
    pf: PartialFunction[BearerSecuredRequest[F], F[Response[F]]]
  )(implicit F: Monad[F]): BearerAuthService[F] =
    Kleisli(req => pf.andThen(OptionT.liftF(_)).applyOrElse(req, Function.const(OptionT.none)))
}
