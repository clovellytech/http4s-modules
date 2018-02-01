package com.clovellytech.featurerequests.db
package sql

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

import domain._

trait VoteSQL {
  def insert(vote: Vote[FeatureId]) : Update0 = sql"""
    insert into vote (feature_request_id, by_user_id, vote, comment)
    values (${vote.featureRequest}, ${vote.userId}, ${vote.vote}, ${vote.comment})
  """.update
}
