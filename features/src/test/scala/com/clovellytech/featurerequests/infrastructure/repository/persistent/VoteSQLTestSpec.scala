package com.clovellytech.featurerequests
package infrastructure.repository.persistent

import java.util.UUID

import cats.effect.IO
import cats.syntax.option._
import org.scalatest._
import doobie.scalatest._
import doobie.util.transactor.Transactor
import domain.votes._
import org.joda.time.DateTime

class VoteSQLTestSpec extends FlatSpec with Matchers with IOChecker {
  val transactor : Transactor[IO] = testTransactor

  import VoteSQL._

  "Vote SQL" should "typecheck" in {
    check(insert(Vote(1L, DateTime.now(), UUID.randomUUID(), 1.toShort.some, "Great idea".some)))
  }
}