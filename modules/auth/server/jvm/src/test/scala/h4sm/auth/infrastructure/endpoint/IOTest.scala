package h4sm.auth.infrastructure.endpoint

import cats.effect.IO
import org.scalatest.funsuite.AnyFunSuiteLike


trait IOTest { this: AnyFunSuiteLike =>
  def testIO(name: String)(t: => IO[Any]) = test(name)(t.unsafeRunSync())
}
