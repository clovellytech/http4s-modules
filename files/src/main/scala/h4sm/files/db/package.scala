package h4sm.files

import java.util.UUID

import scala.reflect.runtime.universe.TypeTag
import cats.syntax.show._
import cats.Show
import doobie.util.Meta

package object db {
  type FileInfoId = UUID

  implicit def backendMeta[A : Show : TypeTag](implicit U : Unshow[A]) : Meta[A] =
    Meta[String].imap(U.unshow(_))(_.show)
}
