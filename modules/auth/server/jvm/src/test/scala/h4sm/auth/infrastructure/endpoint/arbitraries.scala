package h4sm.auth.infrastructure.endpoint

import org.scalacheck._

import h4sm.testutil.arbitraries._
import h4sm.auth.comm.UserRequest

object arbitraries {

  implicit val userRequestArb: Arbitrary[UserRequest] = Arbitrary {
    for {
      username <- nonEmptyString
      password <- nonEmptyString
    } yield UserRequest(username, password)
  }
}
