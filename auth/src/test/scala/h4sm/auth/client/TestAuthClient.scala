package h4sm.auth
package client

import cats.effect.IO
import h4sm.auth.infrastructure.endpoint.UserRequest
import org.http4s.Headers
import org.scalacheck.Arbitrary
import org.scalatest.Assertion
import org.scalatest.prop.PropertyChecks
import infrastructure.endpoint.arbitraries._


trait IOTestAuthClient { this : PropertyChecks =>
  def authClient: AuthClient[IO]

  def withUser[A](u : UserRequest)(f : Headers => IO[A]) : IO[A] = for {
    _ <- authClient.postUser(u)
    login <- authClient.loginUser(u)
    result <- f(authClient.getAuthHeaders(login))
    _ <- authClient.deleteUser(u.username)
  } yield result

  def forAnyUser[A](f : Headers => UserRequest => IO[Assertion]) : Assertion = forAll {
    (u : UserRequest) => withUser(u)(headers => f(headers)(u)).unsafeRunSync()
  }

  def forAnyUser2[A : Arbitrary](f : Headers => (UserRequest, A) => IO[Assertion]) : Assertion = forAll {
    (u: UserRequest, a : A) => withUser(u)(headers => f(headers)(u, a)).unsafeRunSync()
  }
}
