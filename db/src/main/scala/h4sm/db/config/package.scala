package h4sm.db

import cats.{Applicative, ApplicativeError, Functor}
import cats.implicits._
import cats.mtl.{ApplicativeAsk, DefaultApplicativeAsk}
import io.circe.Decoder
import io.circe.config.parser
import io.circe.generic.semiauto._

package object config {
  implicit val dbConfigDecoder : Decoder[DatabaseConfig] = deriveDecoder

  def getPureConfigAsk[F[_], C: Decoder](name : String = "")(implicit
    ev : ApplicativeError[F, Throwable]
  ) : ApplicativeAsk[F, C] =
    new DefaultApplicativeAsk[F, C] {
      val c : F[C] = parser.decodePathF[F, C](name)
      val applicative: Applicative[F] = implicitly
      def ask: F[C] = c
    }

  implicit def ConfigAskFunctor[F[_] : Functor] = new Functor[ApplicativeAsk[F, ?]]{
    def map[AA, B](fa: ApplicativeAsk[F, AA])(f: AA => B) = new DefaultApplicativeAsk[F, B]{
      val applicative: Applicative[F] = fa.applicative
      def ask: F[B] = fa.ask.map(f)
    }
  }
}
