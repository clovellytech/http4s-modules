package h4sm.dbtesting

import cats.effect.IO
import org.scalatest.fixture
import org.scalactic.source.Position

trait IOFixtureTest { self: fixture.FunSuiteLike =>
  class IOResult(name: String){
    def apply(t: () => IO[Any])(implicit pos: Position): Unit = test(name)(() => t().unsafeRunSync())
    def apply(t: self.FixtureParam => IO[Any])(implicit pos: Position): Unit = test(name)((p: FixtureParam) => t(p).unsafeRunSync())
  }

  def testIO(name: String): IOResult = new IOResult(name)
}
