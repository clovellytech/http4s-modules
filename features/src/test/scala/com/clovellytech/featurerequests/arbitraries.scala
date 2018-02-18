package com.clovellytech.featurerequests

import com.clovellytech.featurerequests.domain.requests.FeatureRequest
import org.scalacheck.{Gen, Arbitrary}

object arbitraries {
  implicit val featureRequest: Arbitrary[FeatureRequest] = Arbitrary {
    for {
      s <- Gen.alphaStr
      s2 <- Gen.alphaStr
    } yield FeatureRequest(s, s2)
  }
}
