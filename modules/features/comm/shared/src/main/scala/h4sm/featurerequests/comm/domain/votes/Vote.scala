package h4sm.featurerequests.comm.domain
package votes

import h4sm.auth.comm.authIdTypes.UserId
import features.Feature

final case class VoteRequest(
  featureRequest: FeatureId,
  vote: Option[Short],
  comment: Option[String]
)

final case class VotedFeature(
  featureId: FeatureId,
  feature: Feature,
  downvoteCount: Long,
  upvoteCount: Long
)

final case class Vote(
  featureRequestId: FeatureId,
  userId: Option[UserId],
  vote: Option[Short],
  comment: Option[String],
)
