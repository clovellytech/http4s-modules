package h4sm.featurerequests
package infrastructure.endpoint

import cats.effect.Sync
import cats.implicits._
import h4sm.auth.client.AuthClient
import org.http4s._
import org.http4s.implicits._
import org.http4s.dsl._
import org.http4s.client.dsl._
import domain.requests._
import domain.votes._
import doobie.util.transactor.Transactor
import infrastructure.repository.persistent.{RequestRepositoryInterpreter, VoteRepositoryInterpreter}

class TestRequests[F[_]: Sync](xa: Transactor[F]) extends Http4sDsl[F] with Http4sClientDsl[F] {

  val authTestEndpoints = AuthClient.fromTransactor(xa)

  val authEndpoints = authTestEndpoints.authEndpoints
  val Auth = authEndpoints.Auth

  implicit val rinterp = new RequestRepositoryInterpreter(xa)
  val re = new RequestEndpoints[F]

  val requestEndpoints: HttpRoutes[F] = re.unAuthEndpoints <+> Auth.liftService(re.authEndpoints)

  implicit val vinterp = new VoteRepositoryInterpreter[F](xa)
  val voteEndpoints: HttpRoutes[F] = Auth.liftService(new VoteEndpoints[F].endpoints)

  def addRequest(req: FeatureRequest)(resp : Response[F]): F[Response[F]] = for {
    addReq <- POST(req, Uri.uri("/request"))
    authReq = authTestEndpoints.injectAuthHeader(resp)(addReq)
    resp <- requestEndpoints.orNotFound.run(authReq)
  } yield resp

  def addVote(vote : VoteRequest): F[Response[F]] = for {
    voteReq <- POST(vote, Uri.uri("/vite"))
    resp <- voteEndpoints.orNotFound.run(voteReq)
  } yield resp

  def getRequests: F[Response[F]] = GET(Uri.uri("/request")).flatMap(requestEndpoints.orNotFound run _)
}
