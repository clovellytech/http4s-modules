package h4sm.featurerequests.domain
package requests

import java.time.Instant

import h4sm.db.CRAlgebra
import h4sm.featurerequests.db.domain._
import simulacrum.typeclass

@typeclass
trait RequestRepositoryAlgebra[F[_]] extends CRAlgebra[F, FeatureId, Feature, Instant]{
  def insert(r: Feature): F[Unit]

  def selectWithVoteCounts: F[List[VotedFeature]]
}
