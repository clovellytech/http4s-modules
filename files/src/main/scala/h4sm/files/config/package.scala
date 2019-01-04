package h4sm.files

import cats.{Applicative, ApplicativeError}
import cats.mtl.ApplicativeAsk
import cats.syntax.either._
import cats.syntax.functor._
import com.typesafe.config.ConfigFactory
import h4sm.db.config.DatabaseConfig
import io.circe.Decoder
import io.circe.config.syntax._

package object config {
  type ConfigAsk[F[_]] = ApplicativeAsk[F, FileConfig]
  type DBConfigAsk[F[_]] = ApplicativeAsk[F, DatabaseConfig]
  type ServerConfigAsk[F[_]] = ApplicativeAsk[F, ServerConfig]

  def getConfigAsk[F[_], E: Decoder](name : String)(implicit ev : ApplicativeError[F, Throwable]) : ApplicativeAsk[F, E] = {
    val c = ConfigFactory.load().as[E](name).leftMap(_.asInstanceOf[Throwable]).raiseOrPure[F]
    new ApplicativeAsk[F, E] {
      val applicative: Applicative[F] = Applicative[F]
      def ask: F[E] = c
      def reader[A](f: E => A): F[A] = ask.map(f)
    }
  }
}
