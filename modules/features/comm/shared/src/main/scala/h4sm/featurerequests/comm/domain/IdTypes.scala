package h4sm.featurerequests.comm.domain

trait IdTypes {
  type VoteId = Long
  type FeatureId = Long
}

object featureIdTypes extends IdTypes
