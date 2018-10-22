Http4s Modules
===

This project aims to supply a basket of modules that you can pull from and configure to create a fully functional website using http4s and cats. The main goal is to provide everything in the box needed for this purpose. Instead of requiring you to create your own database schema, each module manages its own namespace of database tables. As you grow your site, ideally as another module in a similar style, you will be able to extend the provided data models by simply joining to them in your queries.

Let's create a server that allows users to log in and submit feature requests for our new site.

```tut:silent
import cats._
import cats.implicits._
import cats.effect.IO
import com.clovellytech._
import doobie.hikari.HikariTransactor
import db.config.DatabaseConfig
```

Initializing Databases
---

Database schemas come pre-packaged with each module. Here we will import the needed modules and initialize all our namespaces on the fly. We'll import the auth module for user accounts and authentication, and the features module for our features request service.

The below will initialize our database schema and create a transactor that we can use for the rest of this documentation. A proper main function that would launch a server is provided as an example in `com.clovellytech.featurerequests.Server`

```tut
val db = pureconfig.loadConfigOrThrow[DatabaseConfig]("db")
val transactor : IO[HikariTransactor[IO]] = for {
	xa <- HikariTransactor.newHikariTransactor[IO](db.driver, db.url, db.user, db.password)
	initSchema = DatabaseConfig.initializeFromTransactor(xa) _
	_ <- initSchema("ct_auth")
	_ <- initSchema("ct_feature_requests")
} yield xa 

val xa = transactor.unsafeRunSync()
```

The `ct_auth` and `ct_featurerequests` schemas are defined in Flyway migrations in the respective resources directories for those modules. Any module can be migrated and initialized this way. Just follow the same convention as is followed in those modules.

Building endpoints
--

```tut
import tsec.passwordhashers.jca.BCrypt
import auth.infrastructure.endpoint._
import featurerequests.infrastructure.endpoint._

val authEndpoints = AuthEndpoints.persistingEndpoints(xa, BCrypt)
val authService = authEndpoints.Auth
val requests = RequestEndpoints.persistingEndpoints(xa)
val votes = VoteEndpoints.persistingEndpoints(xa)

val requestEndpoints = requests.unAuthEndpoints <+> authService.liftService(requests.authEndpoints)

val voteEndpoints = authService.liftService(votes.endpoints)
```

Now we can start sending some requests to our endpoints. Let's create a user by registering, then logging in and fetching user details.

```tut
import org.http4s._
import org.http4s.dsl._
import org.http4s.client.dsl._

// See the endpoint tests in different modules for an examples of extending 
// these dsls more generically.
object ioClient extends Http4sDsl[IO] with Http4sClientDsl[IO]
import ioClient._

val authClient = authEndpoints.endpoints.orNotFound

val userReq = UserRequest("email", "pass".getBytes)
val user : IO[UserDetail] = for {
	registerReq <- POST(uri("/user"), userReq)
	loginReq <- POST(uri("/login"), userReq)
	registerRes <- authClient.run(registerReq)
	loginRes <- authClient.run(loginReq)
	headers = loginRes.headers.filter(_.name.toString.startsWith("Authorization"))
	userReq <- GET(uri("/user"))
	userRes <- authClient.run(userReq.withHeaders(headers))
	_ = println(userRes.status)
	u <- userRes.as[UserDetail]
} yield u

user.unsafeRunSync()
```

And we can see what would happen if the login request were invalid:

```tut
import cats.data.OptionT

val user2 : OptionT[IO, UserDetail] = for {
	loginReq <- OptionT.liftF(POST(uri("/login"), userReq.copy(password="wrong".getBytes)))
	loginRes <- OptionT.liftF(authClient.run(loginReq))
	headers = loginRes.headers.filter(_.name.toString startsWith "Authorization")
	userReq <- OptionT.liftF(GET(uri("/user")))
	userRes <- OptionT.liftF(authClient.run(userReq withHeaders headers))
	_ = println(userRes.status)
	user <- {
		if(userRes.status == Status.Ok) OptionT.liftF(userRes.as[UserDetail]) 
		else OptionT.none[IO, UserDetail]
	}
} yield user

println("User is " + user2.value.unsafeRunSync())
```
