package h4sm.featurerequests.domain.votes

import h4sm.featurerequests.db.domain.FeatureId

final case class VoteRequest(
  featureRequest: FeatureId,
  vote: Option[Short],
  comment: Option[String]
)
