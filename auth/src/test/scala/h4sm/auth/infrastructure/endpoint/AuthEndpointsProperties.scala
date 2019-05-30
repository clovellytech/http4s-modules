package h4sm
package auth.infrastructure
package endpoint

import java.time.{Duration, Instant}

import cats.effect.IO
import doobie.util.transactor.Transactor
import h4sm.auth.client.AuthClient
import h4sm.auth.infrastructure.repository.persistent._
import h4sm.dbtesting.DbFixtureSuite
import org.http4s.Status
import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import arbitraries._
import tsec.authentication.TSecBearerToken

class AuthEndpointsProperties extends DbFixtureSuite with Matchers with ScalaCheckPropertyChecks {
  def schemaNames: Seq[String] = List("ct_auth")

  def client(tr: Transactor[IO]): AuthClient[IO, TSecBearerToken] = {
    implicit val userService = new UserRepositoryInterpreter(tr)
    implicit val tokenService = new TokenRepositoryInterpreter(tr)
    new AuthClient(Authenticators.bearer[IO])
  }

  test("A user should login") { p =>
    val authClient = client(p.transactor)
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

  test("A duplicate registration should fail") { p =>
    val authClient = client(p.transactor)
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

  test("A login create usable session") { p =>
    val authClient = client(p.transactor)
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

  test("A bad password return 400") { p =>
    val authClient = client(p.transactor)
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
