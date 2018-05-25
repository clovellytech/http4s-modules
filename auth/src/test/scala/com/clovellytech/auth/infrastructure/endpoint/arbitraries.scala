package com.clovellytech
package auth.infrastructure.endpoint

import org.scalacheck._

import dbtesting.arbitraries._

object arbitraries {

  implicit val userRequestArb : Arbitrary[UserRequest] = Arbitrary {
    for {
      username <- nonEmptyString
      password <- nonEmptyString
    } yield UserRequest(username, password.getBytes)
  }
}
