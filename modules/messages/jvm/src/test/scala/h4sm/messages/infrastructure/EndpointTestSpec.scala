package h4sm
package messages
package infrastructure.endpoint

import auth.comm.UserRequest
import auth.client.IOTestAuthClientChecks
import auth.comm.arbitraries._
import cats.effect.IO
import testutil.DbFixtureSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import infrastructure.repository.persistent.sql.arbitraries._
import org.http4s.Headers

class MessagesEndpointsTestSpec
    extends Matchers
    with ScalaCheckPropertyChecks
    with DbFixtureSuite
    with IOTestAuthClientChecks {
  def schemaNames: Seq[String] = Seq("ct_auth", "ct_messages")

  test("a user should be able to send a message") { p =>
    new MessageClientRunner[IO] {
      val xa = p.transactor
      forAnyUser3(testAuthClient) {
        implicit h: Headers => (_: UserRequest, u2: UserRequest, m: CreateMessageRequest) =>
          testAuthClient.withUser(u2) { h2 =>
            for {
              uu2 <- authClient.getUser(h2)
              _ <- messageClient.sendMesssage(m.copy(to = uu2.userId))
              s <- messageClient.getInbox
            } yield s.head.to should equal(uu2.userId)
          }
      }
    }
  }
}
