package h4sm.featurerequests
package infrastructure
package endpoint

import cats.effect.{Bracket, Sync}
import cats.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec._
import h4sm.auth.BearerAuthService
import h4sm.featurerequests.infrastructure.repository.persistent.VoteRepositoryInterpreter
import db.domain.Vote
import domain.votes.{VoteRepositoryAlgebra, VoteRequest}
import doobie.util.transactor.Transactor
import tsec.authentication._

class VoteEndpoints[F[_]: Sync: VoteRepositoryAlgebra] extends Http4sDsl[F] {
  def submitVote: BearerAuthService[F] = BearerAuthService {
    case req @ POST -> Root / "vote" asAuthed _ => for {
      voteReq <- req.request.as[VoteRequest]
      vote = Vote(voteReq.featureRequest, req.authenticator.identity.some, voteReq.vote, voteReq.comment)
      _ <- VoteRepositoryAlgebra[F].insert(vote)
      resp <- Ok()
    } yield resp
  }

  def endpoints: BearerAuthService[F] = submitVote
}

object VoteEndpoints {
  def persistingEndpoints[F[_]: Sync: Bracket[?[_], Throwable]](xa: Transactor[F]): VoteEndpoints[F] = {
    implicit val voteRepo = new VoteRepositoryInterpreter(xa)
    new VoteEndpoints[F]
  }
}
