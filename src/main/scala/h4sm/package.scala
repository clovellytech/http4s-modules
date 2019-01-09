import cats._
import cats.mtl.{ApplicativeAsk, DefaultApplicativeAsk}
import cats.effect.Sync
import cats.implicits._
import com.typesafe.config.ConfigFactory
import h4sm.files.config.FileConfig
import io.circe.Decoder
import io.circe.config.syntax._
import io.circe.generic.auto._

package object h4sm {
  type ConfigAsk[F[_]] = MainConfig.ConfigAsk[F]

  def getPureConfigAsk[F[_]: Sync, C: Decoder] : ApplicativeAsk[F, C] =
    new DefaultApplicativeAsk[F, C] {
      val c : F[C] = ConfigFactory.load().as[C].leftWiden[Throwable].raiseOrPure[F]
      val applicative: Applicative[F] = implicitly
      def ask: F[C] = c
    }

  implicit def ConfigAskFunctor[F[_] : Functor] = new Functor[ApplicativeAsk[F, ?]]{
    def map[AA, B](fa: ApplicativeAsk[F, AA])(f: AA => B) = new DefaultApplicativeAsk[F, B]{
      val applicative: Applicative[F] = fa.applicative
      def ask: F[B] = fa.ask.map(f)
    }
  }

  def getConfigAsk[F[_] : Sync] : ConfigAsk[F] = getPureConfigAsk[F, MainConfig]

  /*
   * Given the ConfigAskFunctor, we can easily create child instances for the various
   * components of the system we're building.
   */
  def getFileConfigAsk[F[_] : Sync] : ApplicativeAsk[F, FileConfig] = getConfigAsk[F].map(_.files)
}
