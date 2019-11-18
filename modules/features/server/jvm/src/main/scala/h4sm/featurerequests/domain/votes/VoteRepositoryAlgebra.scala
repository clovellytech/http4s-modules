package h4sm.featurerequests
package domain
package votes

import db.domain._
import h4sm.db.CAlgebra
import h4sm.featurerequests.comm.domain.votes.Vote
import simulacrum.typeclass


@typeclass
trait VoteRepositoryAlgebra[F[_]] extends CAlgebra[F, VoteId, Vote]
