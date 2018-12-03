import cats._
import cats.mtl.{ApplicativeAsk, DefaultApplicativeAsk}
import cats.effect.Sync
import cats.implicits._
import pureconfig.ConfigReader
import h4sm.files.config.FileConfig

import scala.reflect.ClassTag

package object h4sm {
  type ConfigAsk[F[_]] = MainConfig.ConfigAsk[F]

  def getPureConfigAsk[F[_] : Sync, C : ClassTag : ConfigReader] : ApplicativeAsk[F, C] =
    new DefaultApplicativeAsk[F, C] {
      val cfg: Eval[C] = Eval.later(pureconfig.loadConfigOrThrow[C])
      val applicative: Applicative[F] = implicitly
      def ask: F[C] = cfg.value.pure[F]
    }

  implicit def ConfigAskFunctor[F[_] : Functor] = new Functor[ApplicativeAsk[F, ?]]{
    def map[AA, B](fa: ApplicativeAsk[F, AA])(f: AA => B) = new DefaultApplicativeAsk[F, B]{
      val applicative: Applicative[F] = fa.applicative
      def ask: F[B] = fa.ask.map(f)
    }
  }

  implicit def getConfigAsk[F[_] : Sync] : ConfigAsk[F] = getPureConfigAsk[F, MainConfig]

  /*
   * Given the ConfigAskFunctor, we can easily create child instances for the various
   * components of the system we're building.
   */
  implicit def getFileConfigAsk[F[_] : Sync] : ApplicativeAsk[F, FileConfig] = getConfigAsk[F].map(_.files)
}
