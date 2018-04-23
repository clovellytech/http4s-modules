package com.clovellytech.featurerequests
package infrastructure
package endpoint

import org.scalatest._
import cats.effect.IO
import org.http4s._

import com.clovellytech.auth.infrastructure.endpoint.UserRequest
import domain.requests._
import db.sql.testTransactor.testTransactor

class RequestEndpointsTestSpec extends FunSuite with IOTest with Matchers {
  val eps = new TestRequests[IO](testTransactor)
  import eps._

  val user = UserRequest("zak", "password".getBytes)

  val featureRequest = FeatureRequest("Hi", "My good idea")

  testIO("new request") {
    for {
      register <- authTestEndpoints.postUser(user)
      login <- authTestEndpoints.loginUser(user)
      addReq <- addRequest(featureRequest)(login)
    } yield {
      addReq.status should equal (Status.Ok)
    }
  }

  testIO("check requests") {
    for {
      r <- getRequests
      all <- r.as[DefaultResult[List[VotedFeatures]]]
    } yield {
      all.result should not be empty
    }
  }
}
