package h4sm.auth.infrastructure.endpoint

import cats.effect.IO
import org.scalatest.FunSuiteLike


trait IOTest { this: FunSuiteLike =>
  def testIO(name: String)(t: => IO[Any]) = test(name)(t.unsafeRunSync())
}
