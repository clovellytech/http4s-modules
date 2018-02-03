package com.clovellytech.featurerequests.db.domain

import java.util.UUID
import org.joda.time.DateTime

final case class Feature(
  userId: Option[UUID],
  title: String,
  description: String
)

