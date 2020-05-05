package h4sm.testutil

import cats.effect.IO
import doobie.util.transactor.Transactor
import h4sm.db.config.DatabaseConfig
import org.scalatest._
import transactor._
import io.circe.config.parser

trait RandomDbCreation {
  implicit lazy val cs = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)
  def randomSuffixLength: Int = 6
  def getRandomSuffix: String =
    scala.util.Random.alphanumeric.take(randomSuffixLength).mkString.toLowerCase()
  def classNameLength: Int = 6
  // Make sure the db name is only generated one time.
  def dbName: String =
    s"cttdb_${getClass.getSimpleName.toLowerCase.take(classNameLength)}_$getRandomSuffix"
  def config: DatabaseConfig = parser.decodePathF[IO, DatabaseConfig]("db").unsafeRunSync()
  def testConfig: DatabaseConfig = config.copy(databaseName = dbName)
}

trait DbFixtureBeforeAfter extends RandomDbCreation with BeforeAndAfterAll { self: Suite =>
  lazy val cfg = testConfig
  def schemaNames: Seq[String]

  private def beforeIO: IO[Unit] =
    for {
      _ <- createDb[IO](cfg)
      _ <- DatabaseConfig.initialize[IO](cfg)(schemaNames: _*)
    } yield ()

  override def beforeAll(): Unit = beforeIO.unsafeRunSync()

  override def afterAll(): Unit = dropDb[IO](cfg).unsafeRunSync()
}

trait DbFixtureSuite extends RandomDbCreation with funsuite.FixtureAnyFunSuiteLike {
  def schemaNames: Seq[String]
  case class FixtureParam(transactor: Transactor[IO])
  lazy val cfg = testConfig

  override def withFixture(test: OneArgTest) = {
    val transactor: IO[Transactor[IO]] = for {
      _ <- createDb[IO](cfg)
      tr <- getInitializedTransactor(cfg, schemaNames: _*)
    } yield tr

    try withFixture(test.toNoArgTest(FixtureParam(transactor.unsafeRunSync())))
    catch {
      case e: Throwable =>
        println(e)
        e.printStackTrace()
        fail("Failed to create test database")
    } finally dropDb[IO](cfg).unsafeRunSync()
  }
}
