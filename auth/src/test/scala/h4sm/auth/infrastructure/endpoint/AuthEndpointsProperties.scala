package h4sm.auth.infrastructure
package endpoint

import java.time.{Duration, Instant}

import cats.effect.IO
import h4sm.auth.client.AuthClient
import org.http4s.Status
import org.scalatest._
import org.scalatest.prop.PropertyChecks
import arbitraries._

class AuthEndpointsProperties extends FlatSpec with Matchers with PropertyChecks {
  val authClient = AuthClient.fromTransactor(testTransactor)

  "A user" should "login" in {
    forAll { u : UserRequest =>
      val test: IO[Assertion] = for {
        post <- authClient.postUser(u)
        login <- authClient.loginUser(u)
        _ <- authClient.deleteUser(u.username)
      } yield {
        login.status should equal (Status.Ok)
      }

      test.unsafeRunSync()
    }
  }

  "A duplicate registration" should "fail" in {
    forAll { u: UserRequest =>
      val test : IO[Assertion] = for {
        _ <- authClient.postUser(u)
        post <- authClient.postUser(u)
        _ <- authClient.deleteUser(u.username)
      } yield {
        post.status should equal(Status.BadRequest)
      }

      test.unsafeRunSync()
    }
  }

  "A login" should "create usable session" in {
    forAll { u : UserRequest =>
      val test: IO[Assertion] = for {
        post <- authClient.postUser(u)
        login <- authClient.loginUser(u)
        user <- authClient.getUser(u.username, login).getOrElse(fail)
        detail <- user.as[UserDetail]
        _ <- authClient.deleteUser(u.username)
      } yield {
        u.username should equal (detail.username)
        Duration.between(detail.joinTime, Instant.now()).toMillis should be < 1000L
      }

      test.unsafeRunSync()
    }
  }

  "A bad password" should "return 400" in {
    forAll { (u : UserRequest) =>
      for {
        post <- authClient.postUser(u)
        login <- authClient.loginUser(u.copy(password = u.password ++ u.password))
        _ <- authClient.deleteUser(u.username)
      } yield {
        login.status should equal (Status.BadRequest)
      }
    }
  }
}
