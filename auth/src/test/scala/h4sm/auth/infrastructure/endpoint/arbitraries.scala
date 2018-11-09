package h4sm.auth.infrastructure.endpoint

import org.scalacheck._

import h4sm.dbtesting.arbitraries._

object arbitraries {

  implicit val userRequestArb : Arbitrary[UserRequest] = Arbitrary {
    for {
      username <- nonEmptyString
      password <- nonEmptyString
    } yield UserRequest(username, password.getBytes)
  }
}
