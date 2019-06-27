package h4sm.files.domain

import java.util.UUID

import org.scalacheck._
import cats.implicits._
import h4sm.testutil.arbitraries.nonEmptyString
import h4sm.files.infrastructure.endpoint.FileUpload

object arbitraries {
  implicit val backends : Gen[Backend] = Gen.oneOf(Seq(Backend.LocalBackend))

  implicit val backendArb : Arbitrary[Backend] = Arbitrary(backends)

  implicit val uuidArb: Arbitrary[UUID] = Arbitrary(Gen.uuid)

  implicit val fileInfoGen : Gen[FileInfo] = for {
    name <- nonEmptyString
    desc <- nonEmptyString
    filename <- nonEmptyString
    url <- nonEmptyString
    uploadedBy <- Gen.uuid
    isPublic <- Gen.oneOf(Seq(true, false))
    backend <- backends
  } yield FileInfo(name.some, desc.some, filename.some, url.some, uploadedBy, isPublic, backend)

  implicit val fileInfoArb : Arbitrary[FileInfo] = Arbitrary(fileInfoGen)

  implicit val fileUploadArb : Arbitrary[FileUpload] = Arbitrary(for {
    name <- nonEmptyString
    desc <- nonEmptyString
    isPublic <- Gen.oneOf(Seq(true, false))
  } yield FileUpload(name, desc.some, isPublic))
}
