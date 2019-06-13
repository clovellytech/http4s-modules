---
id:     initialSchema
title:  Http4s Modules by Example - Initial Petstore Schema
---

To implement our petstore API, we will use a schema that depends on tables from `ct_auth` and `ct_permissions` existing. These postgres schemas include tables that define users, tokens, and permissions that may be defined by other modules on the users's abilities.

We need new tables in our ct_petstore schema, which will store pets and orders

The below schemas are created in `/h4sm-docs/resources/db/migration/ct_petstore/01-initial-schema.sql`

```sql
create schema if not exists ct_petstore;
create table if not exists ct_petstore.pet (
  pet_id uuid not null primary key default gen_random_uuid(),
  create_date timestamp with time zone not null default now(),
  update_time timestamp with time zone not null default now(),
  name text not null,
  bio text,
  created_by uuid references ct_auth.user (user_id),
  status text not null,
  photo_urls text[]
);

create table if not exists ct_petstore.order (
  order_id uuid not null primary key default gen_random_uuid(),
  pet_id uuid not null references ct_petstore.pet,
  user_id uuid not null references ct_auth.user,
  create_time timestamp with time zone not null default now(),
  ship_date timestamp with time zone
);
```

Both the pet table and the order table reference the users which are already provided. 

Now that we have our initial schema defined, we need to ensure that our schema will be properly migrated, and all dependent schemas will also be created before this one.

Let us create a starter object we will call Server. It will not serve anything yet, but this will be our scaffold that will eventually contain all the components needed to serve our API.

```scala mdoc
import cats.effect.Sync
import cats.implicits._
import h4sm.db.config._
import h4sm.files.config.FileConfig
import io.circe.Decoder
import io.circe.config.parser
import io.circe.generic.auto._  // for parsing our configuration file.

final case class ServerConfig(port: Int, host: String, numThreads: Int)
final case class MainConfig(db: DatabaseConfig, files: FileConfig, server: ServerConfig)

class Server[F[_]: Sync] {
  // I require a Sync for F, because DatabaseConfig.initialize requires a Sync for F, below...

  // These schemas will be migrated in order (table definitions written to database.)
  val schemaNames: Seq[String] = List("ct_auth", "ct_permissions", "ct_files", "ct_petstore")

  def initialize: F[Unit] = for {
    cfg <- parser.decodeF[F, MainConfig]()
    MainConfig(db, fc, ServerConfig(host, port, numThreads)) = cfg
    _ <- DatabaseConfig.initialize[F](db)(schemaNames: _*)
  } yield ()
}

object Server{
  def init[F[_]: Sync]: F[Unit] = new Server[F]().initialize
}
```

Now that we have our initial `Server` class set up, let's try to actually write these table definitions to our database. There are no endpoints to serve yet. Lots will change for this little `Server` class soon.

Note that we get to delay the specification of the effect type `F` in which this code is run until this moment. Here is the first invocation of our program:

```scala mdoc
import cats.effect.IO

val program = Server.init[IO].flatMap(_ => IO(println("Done!")))
program.unsafeRunSync()
```

Our database tables have been migrated to our database, let's [move forward with writing some queries](02-queries.md)
