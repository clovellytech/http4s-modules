package com.clovellytech.featurerequests
package infrastructure.endpoint

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import arbitraries._
import cats.data.OptionT
import cats.effect.IO
import db.sql.testTransactor.testTransactor
import domain.requests._

object RequestEndpointProperties extends Properties("RequestEndpoint") {
  val eps = new TestRequests[IO](testTransactor)
  import eps._

  property("request can be stored") = forAll { (a: FeatureRequest) =>
    val r: IO[OptionT[IO, DefaultResult[List[VotedFeatures]]]] = for {
      addRes <- addRequest(a)
      all <- getRequests
    } yield all.semiflatMap(_.as[DefaultResult[List[VotedFeatures]]])

    val test: IO[OptionT[IO, Boolean]] = r.map(_.map( fs =>
      fs.result != Nil
    ))

    test.unsafeRunSync().getOrElse(false).unsafeRunSync()
  }
}
