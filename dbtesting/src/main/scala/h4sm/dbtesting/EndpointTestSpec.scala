package h4sm
package dbtesting

import cats.effect.IO
import doobie.Transactor
import org.scalatest.Matchers

trait EndpointTestSpec extends DbFixtureSuite with IOFixtureTest with Matchers {
  def transactor: Transactor[IO] = dbtesting.transactor.getTransactor[IO](cfg)
}
