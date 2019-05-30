package h4sm.auth.infrastructure.endpoint

import cats.effect.IO
import org.scalatest._
import org.scalactic.source.Position


trait IOTest { this: FunSuiteLike =>
  def testIO(name: String)(t: => IO[Any]) = test(name)(t.unsafeRunSync())
}

trait IOFixtureTest { self: fixture.FunSuiteLike =>
  class IOResult(name: String){
    def apply(t: () => IO[Any])(implicit pos: Position): Unit = test(name)(() => t().unsafeRunSync())
    def apply(t: self.FixtureParam => IO[Any])(implicit pos: Position): Unit = test(name)((p: FixtureParam) => t(p).unsafeRunSync())
  }

  def testIO(name: String): IOResult = new IOResult(name)
}
