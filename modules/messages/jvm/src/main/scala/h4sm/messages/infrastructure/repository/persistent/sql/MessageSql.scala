package h4sm.messages
package infrastructure.repository.persistent.sql

//import h4sm.messages.domain.arbitraries._
import domain._
import doobie._
import doobie.implicits._
import h4sm.auth.UserId
import h4sm.auth.db.sql._
import java.time.Instant

trait MessageSql {

  def insert(m: UserMessage): Update0 = sql"""
    insert
    into ct_messages.message (from_user_id, to_user_id, content, open_date)
    values (${m.from}, ${m.to}, ${m.text}, ${m.openTime})
  """.update

  def select: Query0[(UserMessage, MessageId, Instant)] = sql"""
    select from_user_id, to_user_id, content, open_date, message_id, create_date
    from ct_messages.message
  """.query

  def byId(messageId: MessageId): Query0[(UserMessage, MessageId, Instant)] = (select.toFragment ++ fr"""
    where message_id = $messageId
  """).query

  def delete(messageId: MessageId): Update0 = sql"""
    delete
    from ct_messages.message
    where message_id = $messageId
  """.update

  def inbox(userId: UserId): Query0[(UserMessage, MessageId, Instant)] = (select.toFragment ++ fr"""
    where to_user_id = $userId
    order by create_date asc
  """).query

  def thread(ua: UserId, ub: UserId): Query0[(UserMessage, MessageId, Instant)] = (select.toFragment ++ fr"""
    where (to_user_id = $ua and from_user_id = $ub) or (to_user_id = $ub and from_user_id = $ua)
    order by create_date desc
  """).query
}
