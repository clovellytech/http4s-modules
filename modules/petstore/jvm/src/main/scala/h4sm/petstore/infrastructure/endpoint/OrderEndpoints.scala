package h4sm.petstore
package infrastructure.endpoint

import cats.effect.Sync
import cats.implicits._
import domain._
import h4sm.auth._
import org.http4s.dsl.Http4sDsl
import tsec.authentication._
import h4sm.auth.domain.tokens._
import AsBaseToken.ops._

class OrderEndpoints[F[_]: Sync: OrderAlgebra, T[_]](auth: UserSecuredRequestHandler[F, T])(implicit 
  baseToken: AsBaseToken[T[UserId]]
) extends Http4sDsl[F] with Codecs[F] {

  def createOrder = UserAuthService[F, T] {
    case req@POST -> Root asAuthed _ => for {
      order <- req.request.as[OrderRequest]
      _ <- OrderAlgebra[F].insert(Order(order.petId, req.authenticator.asBase.identity))
      res <- Ok()
    } yield res
  }

  val authService = createOrder

  val endpoints = auth.liftService(createOrder)
}
