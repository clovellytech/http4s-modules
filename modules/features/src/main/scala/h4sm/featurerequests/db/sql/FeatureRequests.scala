package h4sm
package featurerequests.db
package sql

import java.time.Instant

import db.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import domain._


trait RequestSQL {
  def insert(feature: Feature) : Update0 = sql"""
    insert into ct_feature_requests.feature_request (requesting_user_id, title, description)
    values (${feature.userId}, ${feature.title}, ${feature.description})
  """.update

  def select : Query0[(Feature, FeatureId, Instant)] = sql"""
    select requesting_user_id, title, description, feature_request_id, create_date
    from ct_feature_requests.feature_request
  """.query

  def selectAllWithVoteCounts : Query0[VotedFeature] = sql"""
    with upvotes as
      (select feature_request_id, vote_id as upvote_id
       from ct_feature_requests.vote
       where vote > 0),
      downvotes as
      (select feature_request_id, vote_id as downvote_id
       from ct_feature_requests.vote
       where vote < 0)

    select fs.feature_request_id, fs.requesting_user_id, fs.title, fs.description,
           count(upvote_id) as upvotes, count(downvote_id) as downvotes
    from ct_feature_requests.feature_request fs
    left outer join upvotes using (feature_request_id)
    left outer join downvotes using (feature_request_id)
    group by feature_request_id
    order by fs.create_date
  """.query

  def selectById(featureId : FeatureId) : Query0[(Feature, FeatureId, Instant)] = (select.toFragment ++ sql"""
    where feature_request_id = $featureId
  """).query

  def insertGetId(feature : Feature) : ConnectionIO[FeatureId] = insert(feature).withUniqueGeneratedKeys("feature_id")

}
