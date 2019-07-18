package h4sm
package permissions
package infrastructure.endpoint

import cats.effect.Sync
import cats.implicits._
import h4sm.auth.{UserAuthService, UserId, UserSecuredRequestHandler}
import h4sm.auth.domain.tokens.AsBaseToken
import h4sm.permissions.domain.{Permission, PermissionAlgebra, UserPermissionAlgebra}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import tsec.authentication._

class PermissionEndpoints[F[_]: Sync: UserPermissionAlgebra: PermissionAlgebra, T[_]](
  auth: UserSecuredRequestHandler[F, T]
)(implicit b: AsBaseToken[T[UserId]]) extends Http4sDsl[F] {
  val codecs = new Codecs[F]
  import codecs._

  def createEndpoint: UserAuthService[F, T] = PermissionedRoutes("ct_permissions" -> "admin") {
    case req@POST -> Root asAuthed _ => for {
      perm <- req.request.as[Permission]
      _ <- PermissionAlgebra[F].insert(perm)
      result <- Ok()
    } yield result
  }

  def listEndpoint: UserAuthService[F, T] = PermissionedRoutes("permissions" -> "view") {
    case GET -> Root asAuthed _ => for {
      perms <- PermissionAlgebra[F].select
      sendPerms = perms.map{ case (perm, permid, _) => (perm, permid)}
      res <- Ok(SiteResult(sendPerms))
    } yield res
  }

  def selectByAppEndpoint: UserAuthService[F, T] = PermissionedRoutes("permissions" -> "view") {
    case GET -> Root / appName asAuthed _ => for {
      perms <- PermissionAlgebra[F].selectByAppName(appName)
      res <- Ok(SiteResult(perms))
    } yield res
  }

  def authServices: UserAuthService[F, T] = List(
    createEndpoint,
    listEndpoint,
    selectByAppEndpoint
  ).reduce(_ <+> _)

  def endpoints: HttpRoutes[F] = auth.liftService(authServices)
}
