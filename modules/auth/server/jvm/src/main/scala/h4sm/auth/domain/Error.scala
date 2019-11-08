package h4sm.auth.domain

sealed abstract class Error(val msg: String) extends Throwable
object Error {
  case class BadLogin(override val msg: String = "Bad login") extends Error(msg)
  case class NotFound(override val msg: String = "Not found") extends Error(msg)
  case class Duplicate(override val msg: String = "Duplicate object") extends Error(msg)
}
