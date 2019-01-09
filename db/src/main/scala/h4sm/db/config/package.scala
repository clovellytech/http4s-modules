package h4sm.db

import cats.{Applicative, ApplicativeError, Functor}
import cats.implicits._
import cats.mtl.{ApplicativeAsk, DefaultApplicativeAsk}
import com.typesafe.config.ConfigFactory
import io.circe.Decoder
import io.circe.config.syntax._

package object config {
  def loadConfigF[F[_], C : Decoder](name : String = "")(implicit
    ev: ApplicativeError[F, Throwable]
  ) : F[C] =
    ConfigFactory
      .load()
      .as[C](name)
      .leftWiden[Throwable]
      .raiseOrPure[F]

  def getPureConfigAsk[F[_], C: Decoder](name : String = "")(implicit
    ev : ApplicativeError[F, Throwable]
  ) : ApplicativeAsk[F, C] =
    new DefaultApplicativeAsk[F, C] {
      val c : F[C] = loadConfigF[F, C](name)
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
