package h4sm.files

import cats.Applicative
import cats.mtl.ApplicativeAsk
import cats.syntax.applicative._
import cats.syntax.functor._
import h4sm.db.config.DatabaseConfig
import pureconfig.ConfigReader

import scala.reflect.ClassTag

package object config {
  type ConfigAsk[F[_]] = ApplicativeAsk[F, FileConfig]
  type DBConfigAsk[F[_]] = ApplicativeAsk[F, DatabaseConfig]
  type ServerConfigAsk[F[_]] = ApplicativeAsk[F, ServerConfig]

  def getConfigAsk[F[_] : Applicative, E : ClassTag : ConfigReader](name : String) : ApplicativeAsk[F, E] = {
    val c = pureconfig.loadConfigOrThrow[E](name)
    new ApplicativeAsk[F, E] {
      val applicative: Applicative[F] = Applicative[F]
      def ask: F[E] = c.pure[F]
      def reader[A](f: E => A): F[A] = ask.map(f)
    }
  }
}
