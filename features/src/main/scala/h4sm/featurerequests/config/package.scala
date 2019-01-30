package h4sm.featurerequests

import io.circe.Decoder
import io.circe.generic.semiauto._

package object config {
  implicit val featureRequestConfigDecoder: Decoder[FeatureRequestConfig] = deriveDecoder
}
