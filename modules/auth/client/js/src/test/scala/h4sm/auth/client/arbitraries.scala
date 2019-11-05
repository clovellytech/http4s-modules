package h4sm.auth.client

import h4sm.testutil.arbitraries._
import h4sm.auth.comm._
import org.scalacheck._

object arbitraries {
  implicit val UserRequestArb: Arbitrary[UserRequest] = Arbitrary {
    for {
      username <- nonEmptyString
      password <- nonEmptyString
    } yield UserRequest(username, password)
  }
}
