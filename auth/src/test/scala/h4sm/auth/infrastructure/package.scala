package h4sm
package auth

import cats.effect.IO
import doobie.util.transactor.Transactor

import dbtesting.transactor.getTransactorInitialized

package object infrastructure {
  lazy val testTransactor: Transactor[IO] = getTransactorInitialized[IO]("ct_auth").unsafeRunSync()
}
