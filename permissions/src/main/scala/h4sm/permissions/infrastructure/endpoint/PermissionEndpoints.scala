package h4sm
package permissions
package infrastructure.endpoint

import cats.effect.Sync
import cats.implicits._
import h4sm.auth.BearerAuthService
import h4sm.auth.infrastructure.endpoint.AuthEndpoints
import h4sm.permissions.domain.{Permission, PermissionAlgebra}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import tsec.authentication._


class PermissionEndpoints[F[_] : Sync : PermissionAlgebra, A](ae : AuthEndpoints[F, A]) extends Http4sDsl[F] {
  val F = implicitly[PermissionAlgebra[F]]
  val codecs = new Codecs[F]
  import codecs._

  def createEndpoint : BearerAuthService[F] = PermissionedRoutes("ct_permissions" -> "admin") {
    case req@POST -> Root asAuthed _ => for {
      perm <- req.request.as[Permission]
      _ <- F.insert(perm)
      result <- Ok()
    } yield result
  }

  def listEndpoint : BearerAuthService[F] = PermissionedRoutes("permissions" -> "view") {
    case GET -> Root asAuthed _ => for {
      perms <- F.select
      sendPerms = perms.map{ case (perm, permid, _) => (perm, permid)}
      res <- Ok(SiteResult(sendPerms))
    } yield res
  }

  def selectByAppEndpoint : BearerAuthService[F] = PermissionedRoutes("permissions" -> "view") {
    case GET -> Root / appName asAuthed _ => for {
      perms <- F.selectByAppName(appName)
      res <- Ok(SiteResult(perms))
    } yield res
  }

  def authServices : BearerAuthService[F] = List(
    createEndpoint,
    listEndpoint,
    selectByAppEndpoint
  ).reduce(_ <+> _)

  def endpoints : HttpRoutes[F] = ae.Auth.liftService(authServices)
}
