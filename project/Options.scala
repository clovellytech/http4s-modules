import sbt._

object options {
  val scala212Extra = Seq()
  val scala213Extra = Seq(
    "-Ymacro-annotations"                // For simulacrum
  )

  def scalacExtraOptionsForVersion(version: String): Seq[String] = {
    CrossVersion.partialVersion(version) match {
      case Some((2, major)) if major < 13 => scala212Extra
      case _ => scala213Extra
    }
  }
}
