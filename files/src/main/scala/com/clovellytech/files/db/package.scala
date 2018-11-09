package com.clovellytech.files

import java.util.UUID

import scala.reflect.runtime.universe.TypeTag
import doobie.util.meta.Meta

import cats.syntax.show._
import cats.Show

package object db {
  type FileInfoId = UUID

  implicit def backendMeta[A : Show : TypeTag](implicit U : Unshow[A]) : Meta[A] =
    Meta[String].xmap(U.unshow(_), _.show)
}
