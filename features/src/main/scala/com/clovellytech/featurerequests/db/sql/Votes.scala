package com.clovellytech.featurerequests.db
package sql

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

import domain._

trait VoteSQL {
  def insert(vote: Vote) : Update0 = sql"""
    insert into vote (feature_request_id, by_user_id, vote, comment)
    values (${vote.featureRequestId}, ${vote.userId}, ${vote.vote}, ${vote.comment})
  """.update

  def select(vote: Vote) : Query0[(VoteId, Vote)] = sql"""
    select vote_id, feature_request_id, by_user_id, vote, comment
    from vote
    where feature_request_id = ${vote.featureRequestId} and
          by_user_id = ${vote.userId}
  """.query

  def update(vote: Vote) : Update0 = sql"""
    update vote
    set (vote = ${vote.vote}, comment = ${vote.comment})
    where feature_request_id = ${vote.featureRequestId} and
          by_user_id = ${vote.userId}
  """.update
}
