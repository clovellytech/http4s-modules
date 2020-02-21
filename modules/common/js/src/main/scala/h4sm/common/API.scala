package h4sm.common

import cats.Applicative
import io.circe.{Decoder, Encoder}
import simulacrum.typeclass
import cats.data.StateT

object Status {
  val Ok: Int = 200
  val Unauthorized: Int = 401
  val NotFound: Int = 404
  val Conflict: Int = 409
}

sealed abstract class ClientError extends Throwable
object ClientError {
  final case class Conflict(message: String) extends ClientError
  final case class Unauthorized(message: String) extends ClientError
  final case class Unknown(status: Int, message: String) extends ClientError
}

@typeclass
trait API[F[_]] {
  type Request
  type H = Map[String, String]

  type Session[A] = StateT[F, H, A]

  abstract class Response {
    def a: Request
    def as[T: Decoder]: F[T]
  }
  type Resp <: Response

  def post[A: Encoder](route: String, r: A): F[Resp]
  def get(route: String): F[Resp]

  def postR[A: Encoder](route: String, r: A): F[(H, Resp)]

  def postH[A: Encoder](route: String, r: A, hs: H): F[(H, Resp)]
  def getH(route: String, hs: H): F[(H, Resp)]

  def postT[A: Encoder](route: String, r: A)(implicit F: Applicative[F]): Session[Resp] = StateT {
    hs => postH(route, r, hs)
  }

  def delete(route: String): F[Resp]

  def getT(route: String)(implicit F: Applicative[F]): Session[Resp] = StateT { hs =>
    getH(route, hs)
  }
}
