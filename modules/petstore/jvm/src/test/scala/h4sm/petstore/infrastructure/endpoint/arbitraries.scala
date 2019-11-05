package h4sm.petstore.infrastructure.endpoint

import h4sm.testutil.arbitraries._
import org.scalacheck._

object arbitraries {
  implicit val petReqArb: Arbitrary[PetRequest] = Arbitrary(
    for {
      name <- nonEmptyString
      bio <- Gen.option(nonEmptyString)
      status <- nonEmptyString
    } yield PetRequest(name, bio, status)
  )
}
