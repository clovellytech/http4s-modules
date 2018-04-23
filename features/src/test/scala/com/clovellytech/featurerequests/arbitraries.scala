package com.clovellytech.featurerequests

import com.clovellytech.auth.infrastructure.endpoint.UserRequest
import com.clovellytech.featurerequests.domain.requests.FeatureRequest
import org.scalacheck.{Arbitrary, Gen}

object arbitraries {
  val nonEmptyString = Gen.nonEmptyListOf(Gen.alphaChar).map(_.mkString)

  implicit val featureRequest: Arbitrary[FeatureRequest] = Arbitrary {
    for {
      s <- nonEmptyString
      s2 <- nonEmptyString
    } yield FeatureRequest(s, s2)
  }

  implicit val userRequest : Arbitrary[UserRequest] = Arbitrary {
    for {
      s <- nonEmptyString
      s2 <- nonEmptyString.map(_.getBytes)
    } yield UserRequest(s, s2)
  }
}
