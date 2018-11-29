package h4sm.permissions
package infrastructure.endpoint

import cats.data.Kleisli
import cats.implicits._
import cats.effect.Sync
import org.http4s._
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.client.dsl.Http4sClientDsl
import domain._
import org.http4s.Headers

class PermissionClient[F[_] : Sync, A](es : PermissionEndpoints[F, A]) extends Http4sDsl[F] with Http4sClientDsl[F] {

  val endpoints: Kleisli[F, Request[F], Response[F]] = es.endpoints.orNotFound
  val cs : Codecs[F] = new Codecs
  import cs._

  def post[A : EntityEncoder[F, ?]](a : A, u : Uri)(implicit h : Headers) = POST(a, u, h.toList : _*)

  def addPermission(p : Permission)(implicit h : Headers) : F[Unit] = for {
    req <- post(p, Uri.uri("/"))
    _ <- endpoints.run(req)
  } yield ()
}
