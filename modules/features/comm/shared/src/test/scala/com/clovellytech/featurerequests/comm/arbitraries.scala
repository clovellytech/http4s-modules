package h4sm.featurerequests.comm

import h4sm.featurerequests.comm.domain.features.FeatureRequest
import h4sm.testutil.arbitraries._
import org.scalacheck.Arbitrary

object arbitraries {
  implicit val featureRequest: Arbitrary[FeatureRequest] = Arbitrary {
    for {
      s <- nonEmptyString
      s2 <- nonEmptyString
    } yield FeatureRequest(s, s2)
  }
}
