package com.clovellytech.featurerequests.db

package object sql {
  object requests extends RequestSQL
  object votes extends VoteSQL

//  implicit val instantMeta: Meta[Instant] = Meta[java.sql.Timestamp].xmap(
//    ts => new Instant(ts.getTime),
//    dt => new java.sql.Timestamp(dt.getMillis)
//  )
}
