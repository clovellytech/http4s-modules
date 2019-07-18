package h4sm.files

import simulacrum._


@typeclass
trait Unshow[A]{
  @op("unshow") def unshow(name: String): A
}
