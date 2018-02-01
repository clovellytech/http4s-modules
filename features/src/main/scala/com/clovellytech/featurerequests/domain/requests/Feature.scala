package com.clovellytech.featurerequests.domain
package requests

import java.util.UUID

import org.joda.time.DateTime

final case class Feature(
  userId: UUID,
  title: String,
  description: String,
  createDate : DateTime
)
