package h4sm.petstore
package db.sql

import domain._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import h4sm.db.implicits._
import java.time.Instant

trait PetSql {
  def insert(a: Pet): Update0 = sql"""
    insert into ct_petstore.pet (name, bio, status, created_by, photo_urls)
    values (${a.name}, ${a.bio}, ${a.status}, ${a.createdBy}, ${a.photoUrls})
  """.update

  def insertGetId(a: Pet): ConnectionIO[PetId] = insert(a).withUniqueGeneratedKeys("pet_id")

  def select: Query0[(Pet, PetId, Instant)] = sql"""
    select name, bio, created_by, status, photo_urls, update_time, pet_id, create_time
    from ct_petstore.pet
  """.query

  def selectById(id: PetId): Query0[(Pet, PetId, Instant)] = (select.toFragment ++ fr"""
    where pet_id = $id
  """).query

  def update(id: PetId, pet: Pet): Update0 = sql"""
    update ct_petstore.pet
    set name = ${pet.name}, bio = ${pet.bio}, update_time = now(), status = ${pet.status}, photo_urls = ${pet.photoUrls}
    where pet_id = $id
  """.update

  def selectByName(name: String): Query0[(Pet, PetId, Instant)] = (select.toFragment ++ fr"""
    where name = $name
  """).query

  def delete(id: PetId): Update0 = sql"""
    delete from ct_petstore.pet
    where pet_id = $id
  """.update
}

object PetSql extends PetSql
