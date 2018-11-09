package h4sm.files
package db.sql

import java.util.UUID

import h4sm.files.domain.{Backend, FileInfo}
import org.scalacheck.{Arbitrary, Gen}
import h4sm.dbtesting.arbitraries._
import cats.syntax.option._

object arbitraries {
  implicit val backends : Gen[Backend] = Gen.oneOf(Seq(Backend.LocalBackend))

  implicit val backendArb : Arbitrary[Backend] = Arbitrary(backends)

  implicit val uuidArb: Arbitrary[UUID] = Arbitrary(Gen.uuid)

  implicit val fileInfoArb  : Arbitrary[FileInfo] = Arbitrary {
    for {
      name <- nonEmptyString
      desc <- nonEmptyString
      filename <- nonEmptyString
      url <- nonEmptyString
      uploadedBy <- Gen.uuid
      isPublic <- Gen.oneOf(Seq(true, false))
      backend <- backends
    } yield FileInfo(name.some, desc.some, filename.some, url.some, uploadedBy, isPublic, backend)
  }
}
