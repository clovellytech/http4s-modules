package com.clovellytech.featurerequests.db

import cats.effect.Effect
import cats.syntax.flatMap._
import pureconfig.error.ConfigReaderException
import pureconfig.{ConfigReader, Derivation}

import scala.reflect.ClassTag

package object config {
  def loadConfig[F[_], C : ClassTag](name: String)(implicit E: Effect[F], D: Derivation[ConfigReader[C]]) : F[C] =
    E.delay(pureconfig.loadConfig[C](name)).flatMap{
      case Right(c) => E.pure(c)
      case Left(e) => E.raiseError(new ConfigReaderException[C](e))
    }
}
