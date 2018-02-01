package com.clovellytech.featurerequests.db

import doobie.util.meta.Meta
import org.joda.time.DateTime

package object sql {
  object requests extends RequestSQL
  object votes extends VoteSQL

  implicit val DateTimeMeta: Meta[DateTime] = Meta[java.sql.Timestamp].xmap(
    ts => new DateTime(ts.getTime),
    dt => new java.sql.Timestamp(dt.getMillis)
  )
}
