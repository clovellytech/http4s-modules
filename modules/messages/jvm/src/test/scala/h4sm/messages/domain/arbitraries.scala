package h4sm.messages.domain

import org.scalacheck._
import h4sm.testutil.arbitraries._

object arbitraries {
  implicit val messageArb: Arbitrary[UserMessage] = Arbitrary {
    for {
      from <- Gen.uuid
      to <- Gen.uuid
      text <- nonEmptyString
      open <- Gen.option(arbInstant.arbitrary)
    } yield UserMessage(from, to, text, open)
  }
}