package com.clovellytech.featurerequests.db
package sql

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

import domain._


trait RequestSQL {
  def insert(request: Feature) : Update0 = sql"""
    insert into feature_request (requesting_user_id, title, description)
    values (${request.userId}, ${request.title}, ${request.description})
  """.update

  def selectAll : Query0[Feature] = sql"""
    select requesting_user_id, title, description, create_date
    from feature_requests
  """.query[Feature]

  def selectAllWithVoteCounts : Query0[(Feature, Int)] = sql"""
    select fs.requesting_user_id, fs.title, fs.description, fs.create_date, count(vote_id)
    from feature_requests fs
    left outer join vote vs using (feature_request_id)
    group by feature_request_id
    order by fs.create_date
  """.query[(Feature, Int)]
}

