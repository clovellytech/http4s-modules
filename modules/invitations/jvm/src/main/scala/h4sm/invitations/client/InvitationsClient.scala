package h4sm
package invitations
package client

import cats.effect.Sync
import cats.implicits._
import testutil.infrastructure.endpoints.SessionClientDsl
import infrastructure.endpoint._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._
 
class InvitationsClient[F[_]: Sync, A, T[_]](invitations: InvitationEndpoints[F, A, T]) 
extends Http4sDsl[F] 
with Http4sClientDsl[F] 
with SessionClientDsl[F] {
  val codecs = new Codecs[F]
  import codecs._

  val ins = invitations.endpoints.orNotFound

  def sendInvite(request: InvitationRequest)(implicit headers: Headers): F[Unit] =
    post(request, Uri.uri("/")).flatMap(ins.run).void

  def openInvite(request: InvitationByCodeRequest): F[Unit] = 
    GET(Uri.uri("/") / "open" / request.code / request.toEmail).flatMap(ins.run).void
 
  def signupInvite(request: InviteSignup): F[Unit] = 
    POST(request, Uri.uri("/") / "signup").flatMap(ins.run).void
}
