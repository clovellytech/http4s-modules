package h4sm
package files.client

import auth.client.AuthClientRunner
import cats.effect.{Bracket, ContextShift, Sync}
import db.config.getPureConfigAsk
import files.config.FileConfig
import files.infrastructure.backends._
import files.infrastructure.endpoint.FileEndpoints
import files.domain.FileStoreAlgebra
import io.circe.generic.auto._
import scala.concurrent.ExecutionContext
import tsec.authentication.SecuredRequestHandler

abstract class FilesClientRunner[F[_]: Sync: Bracket[?[_], Throwable]] extends AuthClientRunner[F] {
  implicit lazy val c = getPureConfigAsk[F, FileConfig]("files")
  implicit lazy val fileMetaBackend = new FileMetaService(xa)
  implicit def fileStoreBackend(implicit C: ContextShift[F], ec: ExecutionContext): FileStoreAlgebra[F] = 
    new LocalFileStoreService[F]
  def fileEndpoints(implicit C: ContextShift[F], ec:ExecutionContext) = new FileEndpoints(SecuredRequestHandler(auth))
  def fileClient(implicit C: ContextShift[F], ec: ExecutionContext)= new FilesClient(fileEndpoints)
}
