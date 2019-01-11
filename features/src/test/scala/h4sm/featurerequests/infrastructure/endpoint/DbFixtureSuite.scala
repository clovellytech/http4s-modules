package h4sm.featurerequests.infrastructure.endpoint

import cats.effect.IO
import h4sm.featurerequests.db.sql.testTransactor.{createDb, dropDb}
import org.scalatest._
import h4sm.featurerequests.db.sql.testTransactor._

trait DbFixtureSuite extends fixture.FunSuiteLike {

  def dbName : String

  case class FixtureParam(reqs : TestRequests[IO])

  override def withFixture(test : OneArgTest) = {
    val reqs : IO[TestRequests[IO]] = for {
      _ <- createDb(dbName)
      tr <- getTransactorForDb(dbName)
    } yield new TestRequests[IO](tr)

    try {
      test(FixtureParam(reqs.unsafeRunSync()))
    }
    catch {
      case e : Throwable =>
        println(e)
        e.printStackTrace()
        fail("Failed to create test database")
    }
    finally {
      dropDb(dbName).unsafeRunSync()
    }
  }

}