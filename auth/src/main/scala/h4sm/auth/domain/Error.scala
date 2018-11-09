package h4sm.auth.domain


sealed abstract class Error(msg: String) extends Product with Serializable
object Error {
  case class Duplicate(val msg: String = "Duplicate object") extends Error(msg)
}
