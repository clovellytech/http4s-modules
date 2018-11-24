package h4sm.auth.infrastructure
package endpoint

import org.scalatest._
import cats.effect.IO
import cats.implicits._
import org.http4s.Status
import h4sm.auth.client.AuthClient

class AuthEndpointsTestSpec extends FunSuite with IOTest with Matchers{
  val endpoints = AuthClient.fromTransactor(testTransactor)
  import endpoints._

  val user = UserRequest("zak", "password".getBytes)

  testIO("a signup request should return 200 status"){
    for {
      post <- postUser(user)
      _ <- deleteUser(user.username)
    } yield {
      post.status should equal (Status.Ok)
    }
  }

  testIO("a login request should return 200"){
    for {
      _ <- postUser(user)
      login <- loginUser(user)
      _ <- deleteUser(user.username)
    } yield {
      login.status should equal(Status.Ok)
      login.headers.map(_.name.toString) should contain("Authorization")
    }
  }

  testIO("a user should get a usable session on login"){
    for {
      _ <- postUser(user)
      login <- loginUser(user)
      userResp <- getUser(user.username, login).pure[IO]
      handledResp <- userResp.fold(
        IO.raiseError(_),
        identity
      )
      detail <- handledResp.attemptAs[UserDetail].value
      _ <- deleteUser(user.username)
    } yield {
      login.status should equal (Status.Ok)
      handledResp.status should equal (Status.Ok)
      detail.fold[Assertion](
        e => fail(e),
        _.username should equal(user.username)
      )
    }
  }
}
