package h4sm
package invitations
package infrastructure.repository.persistent.sql

import auth.UserId
import h4sm.auth.db.sql._
import h4sm.auth.comm.authIdTypes._
import domain._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._

trait InvitationSQL {
  def insert(a: Invitation[UserId]): Update0 = sql"""
    insert into ct_invitations.invitation (from_user, to_name, to_email, code, send_time, open_time, accept_time, reject_time)
    values (${a.fromUser}, ${a.toName}, ${a.toEmail}, ${a.code}, ${a.sendDate}, ${a.openDate}, ${a.acceptDate}, ${a.rejectDate})
  """.update

  def insertGetId(a: Invitation[UserId]): ConnectionIO[InvitationId] =
    insert(a).withUniqueGeneratedKeys("invitation_id")

  def insertGetAnnotation(a: Invitation[UserId]): ConnectionIO[(UserId, Instant)] =
    insert(a).withUniqueGeneratedKeys("invitation_id", "create_time")

  def all: Query0[(Invitation[UserId], InvitationId, Instant)] = sql"""
    select from_user, to_name, to_email, code, send_time, open_time, accept_time, reject_time, invitation_id, create_time
    from ct_invitations.invitation
  """.query

  def byId(id: InvitationId): Query0[(Invitation[UserId], InvitationId, Instant)] =
    (all.toFragment ++ fr"""
    where invitation_id = $id
  """).query

  def delete(id: InvitationId): Update0 = sql"""
    delete from ct_invitations.invitation
    where invitation_id = $id
  """.update

  def byCode(toEmail: String, code: String): Query0[(Invitation[UserId], InvitationId, Instant)] =
    (all.toFragment ++ fr"""
    where to_email = $toEmail and code = $code
  """).query

  def updateOpenTime(invitationId: InvitationId): Update0 = sql"""
    update ct_invitations.invitation
    set open_time = now()
    where invitation_id = $invitationId
  """.update

  def fromToEmail(toEmail: String): Query0[(Invitation[UserId], InvitationId, Instant)] =
    (all.toFragment ++ fr"""
    where to_email = $toEmail
  """).query
}
