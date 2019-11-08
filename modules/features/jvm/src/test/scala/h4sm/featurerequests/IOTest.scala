package h4sm.featurerequests

import cats.effect.IO
import org.scalatest.funsuite.AnyFunSuite

trait IOTest { this: AnyFunSuite =>
  def testIO(name: String)(t: => IO[Any]) = test(name)(t.unsafeRunSync())
}
