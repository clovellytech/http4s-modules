package com.clovellytech.auth.infrastructure.endpoint

import cats.effect.IO
import org.scalatest.FunSuite


trait IOTest { this: FunSuite =>
  def testIO(name: String)(t: => IO[Any]) = test(name)(t.unsafeRunSync())
}
