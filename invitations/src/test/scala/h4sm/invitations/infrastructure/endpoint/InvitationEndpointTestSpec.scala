package h4sm
package invitations.infrastructure.endpoint

import auth.UserId
import auth.client.{AuthClient, IOTestAuthClientChecks, TestAuthClient}
import auth.domain.UserService
import auth.domain.tokens.TokenRepositoryAlgebra
import auth.domain.users.UserRepositoryAlgebra
import auth.infrastructure.endpoint.arbitraries._
import auth.infrastructure.endpoint.{Authenticators, UserRequest}
import auth.infrastructure.repository.persistent.{UserRepositoryInterpreter, TokenRepositoryInterpreter}
import cats.effect.{Bracket, Sync, IO}
import cats.implicits._
import dbtesting.EndpointTestSpec
import doobie.Transactor
import invitations.client.InvitationsClient
import invitations.domain._
import invitations.infrastructure.repository.persistent.InvitationRepository
import java.time.Instant
import org.http4s.Headers
import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import tsec.authentication.TSecBearerToken
import tsec.passwordhashers.jca.BCrypt
import tsec.passwordhashers._

class InvitationEndpointTestSpec 
extends EndpointTestSpec
with IOTestAuthClientChecks
with ScalaCheckPropertyChecks 
with Matchers {

  def schemaNames: Seq[String] = List("ct_auth", "ct_invitations")

  trait InvitationClientRunner[F[_]] {
    def xa: Transactor[F]
    implicit def userAlg(implicit F: Bracket[F, Throwable]): UserRepositoryAlgebra[F] = new UserRepositoryInterpreter[F](xa)
    def userService(implicit F: Bracket[F, Throwable], P: PasswordHasher[F, BCrypt]) = new UserService[F, BCrypt](BCrypt)
    implicit def tokenAlg(implicit F: Bracket[F, Throwable]): TokenRepositoryAlgebra[F] = new TokenRepositoryInterpreter[F](xa)
    def auth(implicit F: Sync[F]) = Authenticators.bearer[F]
    implicit def invitationAlg(implicit F: Bracket[F, Throwable]): InvitationAlgebra[F] = new InvitationRepository[F](xa)
    def invitationEndpoints(implicit F: Sync[F]) = new InvitationEndpoints[F, BCrypt, TSecBearerToken](userService, auth)
    def invitationClient(implicit F: Sync[F]) = new InvitationsClient[F, BCrypt, TSecBearerToken](invitationEndpoints)
    def authClient(implicit P: PasswordHasher[F, BCrypt], S: Sync[F]) = new AuthClient[F, BCrypt, TSecBearerToken](userService, auth)
    def testAuthClient(implicit P: PasswordHasher[F, BCrypt], S: Sync[F]): TestAuthClient[F, BCrypt, TSecBearerToken] = new TestAuthClient(authClient)

    def createInvite(toName: String, toUser: UserRequest)(implicit h: Headers, S: Sync[F]): F[(Invitation[UserId], InvitationId, Instant)] = for {
      _ <- invitationClient.sendInvite(InvitationRequest(toName, toUser.username))
      res <- invitationAlg.fromToEmail(toUser.username).getOrElseF(S.raiseError(new Exception("Invitation not found")))
    } yield res   
  }


  test("Existing user can invite a new user") { p => 
    new InvitationClientRunner[IO] { 
      val xa: Transactor[IO] = p.transactor 
      forAnyUser2(testAuthClient) {  implicit h: Headers => (_: UserRequest, ps: (String, UserRequest)) => 
        val (toName, newU) = ps
        createInvite(toName, newU).map {
          case (invite, _, _) => invite.toEmail should equal (newU.username)
        }
      }
    }
  }

  test("Invited user should be able to open an invitation") { p =>
    new InvitationClientRunner[IO] {
      val xa: Transactor[IO] = p.transactor
      forAnyUser2(testAuthClient) { implicit h: Headers => (_: UserRequest, ps: (String, UserRequest)) => 
        val (toName, newU) = ps
        val job = for {
          (invite, _, _) <- createInvite(toName, newU)
          _ <- invitationClient.openInvite(InvitationByCodeRequest(newU.username, invite.code))
          (savedInvite, _, _) <- invitationAlg.fromToEmail(newU.username).getOrElseF(Sync[IO].raiseError(new Exception("invite not found")))
        } yield savedInvite.openDate shouldBe defined

        job.recoverWith {
          case e: Throwable => fail(e)
        }
      }
    }
  }

}
