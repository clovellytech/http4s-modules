package com.clovellytech.featurerequests.domain
package votes

import java.util.UUID

import org.joda.time.DateTime

final case class Vote[A](
  featureRequest : A,
  createDate : DateTime,
  userId : UUID,
  vote : Option[Short],
  comment: Option[String]
)
