package h4sm.auth.infrastructure
package endpoint

import java.time.{Duration, Instant}

import cats.effect.IO
import h4sm.auth.client.AuthClient
import h4sm.auth.infrastructure.repository.persistent._
import h4sm.dbtesting.DbFixtureSuite
import org.http4s.Status
import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import arbitraries._
import h4sm.db.config.DatabaseConfig
import io.circe.config.parser

class AuthEndpointsProperties extends DbFixtureSuite with Matchers with ScalaCheckPropertyChecks {
  implicit val userService = new UserRepositoryInterpreter(testTransactor)
  implicit val tokenService = new TokenRepositoryInterpreter(testTransactor)
  val authenticator = Authenticators.bearer[IO]
  val authClient = new AuthClient(authenticator)
  def schemaNames: Seq[String] = List("ct_auth")
  def config : DatabaseConfig = parser.decodePathF[IO, DatabaseConfig]("db").unsafeRunSync()

  test("A user should login") { _ =>
    forAll { u : UserRequest =>
      val test: IO[Assertion] = for {
        _ <- authClient.postUser(u)
        login <- authClient.loginUser(u)
        _ <- authClient.deleteUser(u.username)
      } yield {
        login.status should equal (Status.Ok)
      }

      test.unsafeRunSync()
    }
  }

  test("A duplicate registration should fail")  { _ =>
    forAll { u: UserRequest =>
      val test : IO[Assertion] = for {
        _ <- authClient.postUser(u)
        post <- authClient.postUser(u)
        _ <- authClient.deleteUser(u.username)
      } yield {
        post.status should equal(Status.Conflict)
      }

      test.unsafeRunSync()
    }
  }

  test("A login create usable session") { _ =>
    forAll { u : UserRequest =>
      val test: IO[Assertion] = for {
        _ <- authClient.postUser(u)
        login <- authClient.loginUser(u)
        user <- authClient.getUser(u.username, login).getOrElse(fail)
        detail <- user.as[UserDetail]
        _ <- authClient.deleteUser(u.username)
      } yield {
        u.username should equal (detail.username)
        Duration.between(detail.joinTime, Instant.now()).toMillis should be < 5000L
      }

      test.unsafeRunSync()
    }
  }

  test("A bad password return 400") { _ =>
    forAll { (u : UserRequest) =>
      for {
        _ <- authClient.postUser(u)
        login <- authClient.loginUser(u.copy(password = u.password ++ u.password))
        _ <- authClient.deleteUser(u.username)
      } yield {
        login.status should equal (Status.BadRequest)
      }
    }
  }
}
