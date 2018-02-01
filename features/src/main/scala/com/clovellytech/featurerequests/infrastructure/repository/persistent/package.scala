package com.clovellytech.featurerequests.infrastructure.repository

import doobie.util.meta.Meta
import org.joda.time.DateTime

package object persistent {
  implicit val DateTimeMeta: Meta[DateTime] = Meta[java.sql.Timestamp].xmap(
    ts => new DateTime(ts.getTime),
    dt => new java.sql.Timestamp(dt.getMillis)
  )
}
