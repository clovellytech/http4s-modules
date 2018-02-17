package com.clovellytech.featurerequests.db
package sql

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import domain._
import org.joda.time.DateTime


trait RequestSQL {
  def insert(request: Feature) : Update0 = sql"""
    insert into feature_request (requesting_user_id, title, description)
    values (${request.userId}, ${request.title}, ${request.description})
  """.update

  def selectAll : Query0[(FeatureId, Feature, DateTime)] = sql"""
    select feature_request_id, requesting_user_id, title, description, create_date
    from feature_request
  """.query

  def selectAllWithVoteCounts : Query0[(FeatureId, Feature, DateTime, Long, Long)] = sql"""
    with upvotes as
      (select feature_request_id, vote_id as upvote_id
       from vote
       where vote > 0),
      downvotes as
      (select feature_request_id, vote_id as downvote_id
       from vote
       where vote < 0)

    select fs.feature_request_id, fs.requesting_user_id, fs.title, fs.description, fs.create_date, count(upvote_id) as upvotes, count(downvote_id) as downvotes
    from feature_request fs
    left outer join upvotes using (feature_request_id)
    left outer join downvotes using (feature_request_id)
    group by feature_request_id
    order by fs.create_date
  """.query
}
