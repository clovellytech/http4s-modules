package com.clovellytech
package auth
package db.sql

import cats.syntax.option._
import com.clovellytech.auth.db.domain.User
import org.scalacheck.{Arbitrary, Gen}
import dbtesting.arbitraries._
import tsec.authentication.TSecBearerToken
import tsec.common.SecureRandomId

object arbitraries {

  implicit val userIdArb : Arbitrary[UserId] = Arbitrary(Gen.uuid)

  implicit val secureRandomIdArb : Arbitrary[SecureRandomId] = Arbitrary {
    Gen.posNum[Int].map(num => SecureRandomId(num.toString))
  }

  implicit val bearerTokenArb : Arbitrary[BearerToken] = Arbitrary {
    for {
      sid <- secureRandomIdArb.arbitrary
      uuid <- Gen.uuid
      time <- arbInstant.arbitrary
      otherTime <- arbInstant.arbitrary
    } yield TSecBearerToken(sid, uuid, time, otherTime.some)
  }

  implicit val userArb : Arbitrary[User] = Arbitrary {
    for {
      name <- nonEmptyString
      hash <- nonEmptyString
    } yield User(name, hash.getBytes)
  }
}


