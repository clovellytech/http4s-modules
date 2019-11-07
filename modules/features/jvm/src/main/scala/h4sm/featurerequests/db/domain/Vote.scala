package h4sm
package featurerequests.db.domain

import auth.UserId

final case class Vote(
    featureRequestId: FeatureId,
    userId: Option[UserId],
    vote: Option[Short],
    comment: Option[String],
)

final case class VotedFeature(
    featureId: FeatureId,
    feature: Feature,
    downvoteCount: Long,
    upvoteCount: Long,
)
