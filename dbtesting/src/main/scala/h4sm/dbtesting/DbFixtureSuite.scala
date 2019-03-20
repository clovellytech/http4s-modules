package h4sm.dbtesting

import cats.effect.{ContextShift, IO}
import doobie.util.transactor.Transactor
import h4sm.db.config.DatabaseConfig
import org.scalatest._
import transactor._

trait DbFixtureSuite extends fixture.FunSuiteLike {
  implicit def cs : ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)
  def schemaNames : Seq[String]
  def dbName : String = "ct_test_db_" + this.getClass.getSimpleName.toLowerCase
  def config : DatabaseConfig

  case class FixtureParam(transactor : Transactor[IO])

  override def withFixture(test : OneArgTest) = {
    val transactor : IO[Transactor[IO]] = for {
      _ <- createDb[IO](config, dbName)
      tr <- getTransactorForDb[IO](dbName, schemaNames : _*)
    } yield tr

    try {
      test(FixtureParam(transactor.unsafeRunSync()))
    }
    catch {
      case e : Throwable =>
        println(e)
        e.printStackTrace()
        fail("Failed to create test database")
    }
    finally {
      dropDb[IO](config, dbName).unsafeRunSync()
    }
  }
}
