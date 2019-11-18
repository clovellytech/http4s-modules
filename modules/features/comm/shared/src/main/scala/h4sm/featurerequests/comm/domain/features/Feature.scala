package h4sm.featurerequests.comm.domain.features

import h4sm.auth.comm.authIdTypes._

final case class FeatureRequest(
    title: String,
    description: String,
)

final case class Feature(
    userId: Option[UserId],
    title: String,
    description: String,
)
