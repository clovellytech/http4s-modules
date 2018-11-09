package h4sm.featurerequests.db.domain

import java.util.UUID

final case class Vote(
  featureRequestId: FeatureId,
  userId : Option[UUID],
  vote : Option[Short],
  comment: Option[String]
)
