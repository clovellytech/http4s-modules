package com.clovellytech.featurerequests.db
package sql

import java.util.UUID

import cats.effect.IO
import cats.syntax.option._
import com.clovellytech.featurerequests.db.domain.Vote
import doobie.scalatest.IOChecker
import doobie.util.transactor.Transactor
import org.scalatest._

import votes._

class VoteSQLTestSpec extends FlatSpec with Matchers with IOChecker {
  val transactor: Transactor[IO] = testTransactor.testTransactor

  "Vote SQL" should "typecheck" in {
    check(insert(Vote(1L, UUID.randomUUID().some, 1.toShort.some, "Great idea".some)))
  }
}
