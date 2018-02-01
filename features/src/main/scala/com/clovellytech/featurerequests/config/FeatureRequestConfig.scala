package com.clovellytech.featurerequests
package config

import db.DatabaseConfig

import cats.effect.Effect
import cats.implicits._
import pureconfig.error.ConfigReaderException

case class FeatureRequestConfig(db: DatabaseConfig)

object FeatureRequestConfig {

  import pureconfig._

  /**
    * Loads the pet store config using PureConfig.  If configuration is invalid we will
    * return an error.  This should halt the application from starting up.
    */
  def load[F[_]](implicit E: Effect[F]): F[FeatureRequestConfig] =
    E.delay(loadConfig[FeatureRequestConfig]("featurerequests")).flatMap {
      case Right(ok) => E.pure(ok)
      case Left(e) => E.raiseError(new ConfigReaderException[FeatureRequestConfig](e))
    }
}
