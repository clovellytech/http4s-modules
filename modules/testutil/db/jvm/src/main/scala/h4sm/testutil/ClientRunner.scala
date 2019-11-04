package h4sm
package testutil

import doobie.Transactor


trait ClientRunner[F[_]] {
  def xa: Transactor[F]
}
