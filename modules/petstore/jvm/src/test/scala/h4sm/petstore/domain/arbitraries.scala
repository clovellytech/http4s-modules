package h4sm.petstore
package domain

import org.scalacheck._
import h4sm.testutil.arbitraries._

object arbitraries {
  implicit val arbPet: Arbitrary[Pet] = Arbitrary(
    for {
      name <- nonEmptyString
      bio <- Gen.option(nonEmptyString)
      createdBy <- Gen.uuid
      status <- nonEmptyString
      photoUrls <- Gen.listOf(nonEmptyString)
      updateTime <- Gen.option(arbInstant.arbitrary)
    } yield Pet(name, bio, createdBy, status, photoUrls, updateTime),
  )

  implicit val arbOrder: Arbitrary[Order] = Arbitrary(
    for {
      petId <- Gen.uuid
      userId <- Gen.uuid
      shipTime <- Gen.option(arbInstant.arbitrary)
    } yield Order(petId, userId, shipTime),
  )
}
