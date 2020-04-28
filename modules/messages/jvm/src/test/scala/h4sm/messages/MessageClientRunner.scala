package h4sm
package messages

import auth.client.AuthClientRunner
import auth.domain.tokens._
import cats.effect.{Bracket, Sync}
import tsec.authentication.TSecBearerToken
import h4sm.messages.infrastructure.repository.persistent.MessageRepository
import h4sm.messages.domain.MessageAlgebra
import h4sm.messages.infrastructure.endpoint.MessageEndpoints
import h4sm.messages.client.MessageClient
import tsec.authentication.SecuredRequestHandler

abstract class MessageClientRunner[F[_]: Sync: Bracket[?[_], Throwable]]
    extends AuthClientRunner[F] {
  implicit lazy val messageAlg: MessageAlgebra[F] = new MessageRepository[F](xa)
  def messageEndpoints = new MessageEndpoints[F, TSecBearerToken]
  def messageClient = new MessageClient(messageEndpoints, SecuredRequestHandler(auth))

}
