package com.clovellytech.featurerequests.domain.votes

import com.clovellytech.featurerequests.db.domain.FeatureId

final case class VoteRequest(
  featureRequest : FeatureId,
  vote : Option[Short],
  comment: Option[String]
)
