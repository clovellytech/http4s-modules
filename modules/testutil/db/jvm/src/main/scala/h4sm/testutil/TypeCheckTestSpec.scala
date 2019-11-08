package h4sm
package testutil

import cats.effect.IO
import doobie.scalatest.IOChecker
import org.scalatest.funsuite

trait TypeCheckTestSpec extends funsuite.AnyFunSuite with DbFixtureBeforeAfter with IOChecker {
  def transactor: doobie.Transactor[IO] = testutil.transactor.getTransactor[IO](cfg)
}
