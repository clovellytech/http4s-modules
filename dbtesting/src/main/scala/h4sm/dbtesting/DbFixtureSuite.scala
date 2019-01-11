package h4sm.dbtesting

import cats.effect.{ContextShift, IO}
import doobie.util.transactor.Transactor
import org.scalatest._
import transactor._

trait DbFixtureSuite extends fixture.FunSuiteLike {
  def dbName : String
  implicit def cs : ContextShift[IO]
  def schemaNames : Seq[String]

  case class FixtureParam(transactor : Transactor[IO])

  override def withFixture(test : OneArgTest) = {
    val transactor : IO[Transactor[IO]] = for {
      _ <- createDb[IO](dbName)
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
      dropDb[IO](dbName).unsafeRunSync()
    }
  }

}
