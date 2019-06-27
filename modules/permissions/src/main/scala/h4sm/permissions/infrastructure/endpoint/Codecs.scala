package h4sm.permissions.infrastructure.endpoint

import cats.effect.Sync
import h4sm.permissions.domain.Permission
import io.circe._
import io.circe.generic.semiauto._
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._


class Codecs[F[_] : Sync] {
  implicit val permissionsEncoder : Encoder[Permission] = deriveEncoder
  implicit val permissionsDecoder : Decoder[Permission] = deriveDecoder
  implicit val permissionsEnc : EntityEncoder[F, Permission] = jsonEncoderOf
  implicit val permissionsDec : EntityDecoder[F, Permission] = jsonOf

  implicit def siteResultEncoder[A : Encoder] : Encoder[SiteResult[A]] = deriveEncoder
  implicit def siteResultDecoder[A : Decoder] : Decoder[SiteResult[A]] = deriveDecoder
  implicit def siteResultEnc[A : Encoder] : EntityEncoder[F, SiteResult[A]] = jsonEncoderOf
  implicit def siteResultDec[A : Decoder] : EntityDecoder[F, SiteResult[A]] = jsonOf
}
