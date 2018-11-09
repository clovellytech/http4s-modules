package h4sm.featurerequests.db

import cats.syntax.option._
import org.scalacheck.{Arbitrary, Gen}

import h4sm.dbtesting.arbitraries._
import domain._

object arbitraries {
  implicit val featureArb : Arbitrary[Feature] = Arbitrary {
    for {
      uuid <- Gen.uuid
      title <- nonEmptyString
      desc <- nonEmptyString
    } yield Feature(uuid.some, title, desc)
  }

  implicit val voteArb : Arbitrary[Vote] = Arbitrary {
    for {
      fid <- Gen.posNum[FeatureId]
      uid <- Gen.uuid
      vote <- Gen.posNum[Short]
      comment <- nonEmptyString
    } yield Vote(fid, uid.some, vote.some, comment.some)
  }
}
