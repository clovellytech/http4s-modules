package com.clovellytech.auth.infrastructure
package endpoint

import org.scalatest._

import cats.effect.IO
import cats.implicits._
import org.http4s.Status

import com.clovellytech.auth.testing.AuthTestEndpoints

class AuthEndpointsTestSpec extends FunSuite with IOTest with Matchers{
  val endpoints = new AuthTestEndpoints(testTransactor)

  val user = UserRequest("zak", "password".getBytes)

  testIO("a signup request should return 200 status"){
    endpoints.postUser(user).map(_.status should equal (Status.Ok))
  }

  testIO("a login request should return 200"){
    endpoints.loginUser(user).map{
      r =>
        r.status should equal (Status.Ok)
        r.headers.map(_.name.toString) should contain ("Authorization")
    }
  }

//  testIO("a user lookup should fail if not authenticated"){
//    endpoints.getUser(user.username).fold(
//      x => IO.raiseError(x),
//      _.map(_.status should equal (Status.Unauthorized))
//    )
//  }

  testIO("a user should get a usable session on login"){
    for {
      login <- endpoints.loginUser(user)
      userResp <- endpoints.getUser(user.username, login).pure[IO]
      handledResp <- userResp.fold(
        IO.raiseError(_),
        identity
      )
      detail <- handledResp.attemptAs[UserDetail].value
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
