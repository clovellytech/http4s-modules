package h4sm
package testutil

import cats.effect.IO
import doobie.Transactor
import org.scalatest.matchers.should.Matchers

trait EndpointTestSpec extends DbFixtureSuite with IOFixtureTest with Matchers {
  def transactor: Transactor[IO] = testutil.transactor.getTransactor[IO](cfg)
}
