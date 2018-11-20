package h4sm.auth.domain


sealed abstract class Error(msg: String) extends Throwable
object Error {
  case class BadLogin(val msg : String = "Bad login") extends Error(msg)
  case class NotFound(val msg : String = "Not found") extends Error(msg)
  case class Duplicate(val msg: String = "Duplicate object") extends Error(msg)
}
