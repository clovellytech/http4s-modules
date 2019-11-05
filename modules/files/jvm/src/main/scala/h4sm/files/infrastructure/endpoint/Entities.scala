package h4sm.files
package infrastructure.endpoint

final case class FileUpload(
  name: String,
  description: Option[String],
  isPublic: Boolean
)
