package h4sm
package featurerequests.db.domain

import auth.UserId

final case class Feature(
  userId: Option[UserId],
  title: String,
  description: String
)

