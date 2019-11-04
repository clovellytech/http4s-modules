package h4sm
package invitations.infrastructure.repository.persistent.sql

import testutil.arbitraries._
import h4sm.invitations.domain.Invitation
import org.scalacheck._

object arbitraries {
  implicit def invitationArbitrary[A](implicit A: Arbitrary[A]): Arbitrary[Invitation[A]] = Arbitrary {
    for {
      fromUser <- A.arbitrary
      toName <- nonEmptyString
      toEmail <- nonEmptyString
      code <- nonEmptyString
      sendDate <- Gen.option(arbInstant.arbitrary)
      openDate <- Gen.option(arbInstant.arbitrary)
      acceptDate <- Gen.option(arbInstant.arbitrary)
      rejectDate <- Gen.option(arbInstant.arbitrary)
    } yield Invitation(fromUser, toName, toEmail, code, sendDate, openDate, acceptDate, rejectDate)
  }
}
