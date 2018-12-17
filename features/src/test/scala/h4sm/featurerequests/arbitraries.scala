package h4sm.featurerequests

import h4sm.auth.infrastructure.endpoint.UserRequest
import h4sm.featurerequests.domain.requests.FeatureRequest
import org.scalacheck.Arbitrary
import h4sm.dbtesting.arbitraries._


object arbitraries {
  implicit val featureRequest: Arbitrary[FeatureRequest] = Arbitrary {
    for {
      s <- nonEmptyString
      s2 <- nonEmptyString
    } yield FeatureRequest(s, s2)
  }

  implicit val userRequest : Arbitrary[UserRequest] = Arbitrary {
    for {
      s <- nonEmptyString
      s2 <- nonEmptyString
    } yield UserRequest(s, s2)
  }
}
