package h4sm.files.domain

import cats.mtl.ApplicativeAsk
import cats.{MonadError, Show}
import h4sm.auth.UserId
import h4sm.files.Unshow

trait Backend
object Backend{
  case object LocalBackend extends Backend
  case object NoBackend extends Backend

  val LOCAL = "local"
  val NONE = "none"

  implicit val backendShow: Show[Backend] = Show.show{
    case LocalBackend => LOCAL
    case _ => NONE
  }

  implicit val backendUnshow: Unshow[Backend] = new Unshow[Backend] {
    def unshow(name: String): Backend = name match {
      case "local" => LocalBackend
      case _ => NoBackend
    }
  }
}

final case class FileInfo(
  name: Option[String],
  description: Option[String],
  filename: Option[String],
  uri: Option[String],
  uploadedBy: UserId,
  isPublic: Boolean,
  backend: Backend = Backend.LocalBackend
)

sealed abstract class Error(val message: String) extends Throwable with Product with Serializable

object Error {
  final case class InvalidUserInput(override val message: String) extends Error(message)
  final case class FileNotExistError(override val message: String) extends Error(message)
  final case class UnknownError(override val message: String) extends Error(message)

  def invalidUserInput(message: String): Throwable = InvalidUserInput(message)
  def fileNotExistError(message: String): Throwable = FileNotExistError(message)

  def fromThrowable(t: Throwable): Error = UnknownError(t.getMessage())

  type DBError[F[_]] = MonadError[F, Error]
  val DBError = MonadError

  type IOError[F[_]] = ApplicativeAsk[F, Error]
  val IOError = ApplicativeAsk
}

