Feature Requests Intro
======

Compiling this documentation
---

run `docs/tut` in `sbt`


Running
----

run `~features/reStart` in `sbt`


Testing
----

First some initial imports to bring in `cats`, `http4s` things including client dsl, and our repositories and endpoints:
```tut:silent
import cats._, cats.implicits._
import cats.data.OptionT
import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.client.dsl.io._

import com.clovellytech.featurerequests._
import config._
import db.config._
import infrastructure.endpoint._
import domain._
import domain.requests._
import infrastructure.repository.persistent._
```



Let's build our endpoints for our requests:
```tut
val requestEndpoints: IO[HttpService[IO]] = for {
    cfg <- loadConfig[IO, FeatureRequestConfig]("featurerequests")
    xa <- db.getTransactor(cfg.db)
} yield {
    val interp = new RequestRepositoryInterpreter[IO](xa)
    val service = new RequestService[IO](interp)
    new RequestEndpoints[IO].endpoints(service)
}
```

Now, let's get some requests:
```tut
val allFeatures: IO[Option[DefaultResult[List[VotedFeatures]]]] = for {
    eps <- requestEndpoints
    req <- GET(uri("/requests"))
    res <- eps.run(req).value
    features <- res.traverse(_.as[DefaultResult[List[VotedFeatures]]])
} yield features

OptionT(allFeatures).map(_.result take 3 foreach println).value.unsafeRunSync

```

This shows a sample of the features that have been requested, with the dates of each request, and the number of upvotes and downvotes each feature has received.
