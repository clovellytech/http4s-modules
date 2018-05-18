package com.clovellytech.db

import cats.effect.Sync
import cats.syntax.flatMap._
import pureconfig.error.ConfigReaderException
import pureconfig.{ConfigReader, Derivation}

import scala.reflect.ClassTag

package object config {
  def loadConfig[F[_], C : ClassTag](name: String)(implicit S: Sync[F], D: Derivation[ConfigReader[C]]) : F[C] =
    S.delay(pureconfig.loadConfig[C](name)).flatMap{
      case Right(c) => S.pure(c)
      case Left(e) => S.raiseError(new ConfigReaderException[C](e))
    }
}
