package h4sm.files
package infrastructure
package endpoint

import java.io.{ByteArrayOutputStream, File, PrintStream}

import cats.effect.IO
import cats.implicits._
import doobie.implicits._
import h4sm.auth.client.{AuthClient, IOTestAuthClient}
import h4sm.auth.infrastructure.endpoint.{AuthEndpoints, UserRequest}
import h4sm.db.config._
import h4sm.files.config.FileConfig
import h4sm.files.domain.FileInfo
import h4sm.files.infrastructure.backends._
import h4sm.files.db.sql.{files => filesSql}
import h4sm.files.db.sql.arbitraries._
import io.circe.generic.auto._
import org.scalatest._
import org.scalatest.prop.PropertyChecks
import tsec.passwordhashers.jca.BCrypt


import scala.concurrent.ExecutionContext.Implicits.global

class EndpointsTestSpec extends FlatSpec with Matchers with PropertyChecks with IOTestAuthClient {
  val xa = testTransactor
  implicit val cs = IO.contextShift(global)
  val authEndpoints = AuthEndpoints.persistingEndpoints(xa, BCrypt)
  val authClient = AuthClient.fromTransactor(xa)

  implicit val c = getPureConfigAsk[IO, FileConfig]("files")
  implicit val fileMetaBackend = new FileMetaService[IO](xa)
  implicit val fileStoreBackend = new LocalFileStoreService[IO]
  val fileEndpoints = new FileEndpoints[IO](authEndpoints)
  val fileClient = new FilesClient[IO](fileEndpoints)
  val textFile = new File(getClass.getResource("/testUpload.txt").toURI)

  "A user with no files" should "be able to retrieve empty list of files" in forAnyUser { implicit headers => _ =>
    fileClient.listFiles().map(_.result should be (empty))
  }

  "A user" should "be able to upload a file" in forAnyUser2 { implicit headers => (_: UserRequest, fi: FileInfo) =>
    for {
      upload <- fileClient.postFile(fi, textFile)
      _ <- upload.result.traverse(filesSql.deleteById(_).run).transact(xa)
    } yield {
      upload.result should not be (empty)
    }
  }

  "A user with files" should "get a list of files" in forAnyUser2 { implicit headers => (_ : UserRequest, fi: FileInfo) =>
    for {
      upload <- fileClient.postFile(fi, textFile)
      fs <- fileClient.listFiles()
      _ <- upload.result.traverse(filesSql.deleteById(_).run).transact(xa)
    } yield {
      fs.result should not be empty
    }
  }

  "A user" should "get a specific file" in forAnyUser2 { implicit headers => (_: UserRequest, fi : FileInfo) =>
    for {
      upload <- fileClient.postFile(fi, textFile)
      fs <- fileClient.listFiles()
      (fid, filename) = {
        val (fid, FileInfo(_, _, filename, _, _, _, _)) = fs.result.head
        (fid, filename.getOrElse("download"))
      }
      f <- fileClient.getFile(fid, filename)
      bs = {
        val bytes = f
        val bs = new ByteArrayOutputStream()
        val ps = new PrintStream(bs)
        bytes.chunks.to(fs2.Sink[IO, fs2.Chunk[Byte]](c => IO(ps.write(c.toArray)))).compile.drain.unsafeRunSync()
        bs
      }
      _ <- upload.result.traverse(filesSql.deleteById(_).run).transact(xa)
    } yield {
      bs.toString should not be empty
    }
  }
}
