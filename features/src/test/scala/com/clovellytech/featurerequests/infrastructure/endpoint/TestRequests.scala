package com.clovellytech.featurerequests
package infrastructure.endpoint

import cats.effect.Sync
import cats.implicits._
import com.clovellytech.auth.testing.AuthTestEndpoints
import org.http4s._
import org.http4s.dsl._
import org.http4s.client.dsl._
import domain.requests._
import domain.votes._
import doobie.util.transactor.Transactor
import infrastructure.repository.persistent.{RequestRepositoryInterpreter, VoteRepositoryInterpreter}

class TestRequests[F[_]: Sync](xa: Transactor[F]) extends Http4sDsl[F] with Http4sClientDsl[F] {

  val authTestEndpoints = new AuthTestEndpoints(xa)

  val authEndpoints = authTestEndpoints.authEndpoints
  val Auth = authEndpoints.Auth

  val rinterp = new RequestRepositoryInterpreter(xa)
  val rservice = new RequestService(rinterp)
  val re = new RequestEndpoints(rservice)

  val requestEndpoints: HttpService[F] = re.unAuthEndpoints <+> Auth.liftService(re.authEndpoints)

  val vinterp = new VoteRepositoryInterpreter[F](xa)
  val service = new VoteService[F](vinterp)
  val voteEndpoints: HttpService[F] = Auth.liftService(new VoteEndpoints[F](service).endpoints)

  def addRequest(req: FeatureRequest)(resp : Response[F]): F[Response[F]] = for {
    addReq <- POST(uri("/request"), req)
    authReq = authTestEndpoints.injectAuthHeader(resp)(addReq)
    resp <- requestEndpoints.orNotFound.run(authReq)
  } yield resp

  def addVote(vote : VoteRequest)(resp : Response[F]): F[Response[F]] = for {
    voteReq <- POST(uri("/vite"), vote)
    resp <- voteEndpoints.orNotFound.run(voteReq)
  } yield resp

  def getRequests: F[Response[F]] = GET(uri("/request")).flatMap(requestEndpoints.orNotFound run _)
}
