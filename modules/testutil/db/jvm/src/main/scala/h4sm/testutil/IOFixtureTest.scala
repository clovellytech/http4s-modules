package h4sm.testutil

import cats.effect.IO
import org.scalatest.funsuite
import org.scalactic.source.Position

trait IOFixtureTest { self: funsuite.FixtureAnyFunSuiteLike =>
  class IOResult(name: String) {
    def apply(t: () => IO[Any])(implicit pos: Position): Unit =
      test(name)(() => t().unsafeRunSync())
    def apply(t: self.FixtureParam => IO[Any])(implicit pos: Position): Unit =
      test(name)((p: FixtureParam) => t(p).unsafeRunSync())
  }

  def testIO(name: String): IOResult = new IOResult(name)
}
