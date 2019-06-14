package h4sm
package permissions
package infrastructure.endpoint

import cats.data.Kleisli
import cats.implicits._
import cats.effect.Sync
import org.http4s._
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.client.dsl.Http4sClientDsl
import domain._

import dbtesting.infrastructure.endpoints._

class PermissionClient[F[_] : Sync, Alg, T[_]](es : PermissionEndpoints[F, Alg, T]) extends Http4sDsl[F] with Http4sClientDsl[F] with TestClientDsl[F] {

  val endpoints: Kleisli[F, Request[F], Response[F]] = es.endpoints.orNotFound
  val cs : Codecs[F] = new Codecs
  import cs._

  def addPermission(p : Permission)(implicit h : Headers) : F[Unit] = for {
    req <- post(p, Uri.uri("/"))
    resp <- endpoints.run(req)
    _ <- passOk(resp)
  } yield ()

  def getPermissions(appName : String): F[List[(Permission, PermissionId)]] = for {
    u <- Uri.fromString(s"/$appName").leftWiden[Throwable].raiseOrPure[F]
    req <- GET(u)
    res <- endpoints.run(req)
    perms <- res.as[SiteResult[List[(Permission, PermissionId)]]]
  } yield perms.result
}
