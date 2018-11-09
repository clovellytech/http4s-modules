h4sm - Scala Http4s Modules
============

Create composable microservices or standalone servers with ease using Http4s, cats, Flyway, and PostgreSQL.

Basic Idea
---
To provide a library of production ready modules, complete with database schema, that can be composed and mounted as `HttpService`s. Modules included so far:

* auth - A complete user authentication implementation using `tsec`. Only uses Bearer tokens for now.
* features - An example library that allows users to submit feature requests and vote on features.
* files - A complete file upload and retrieval module. Only local file storage is implemented so far. AWS or other backends can be added by providing a typeclass instance.
* more to come! Something you would like to see here? Submit an issue! 

Create your server!
---

See the example server in `/examples/votingapp`, for a simple demo server that can authenticate users and allow them to post feature requests and vote on features. Initializing the database schema is as simple as including the line:

```
      _ <- Stream.eval(h4sm.featurerequests.db.initializeAll(xa.kernel))
```

The feature requests module has a dependency on the `auth` module. This line causes the auth module schema to be migrated against the db, and then the feature requests module to be migrated.

Get Started
---
Releases coming soon... For now, just clone and publishLocal. Then add:

```
"com.clovellytech" % "h4sm-auth" % "0.0.7",
"com.clovellytech" % "h4sm-features" % "0.0.7",
"com.clovellytech" % "h4sm-files" % "0.0.7"
```

Setup
---
* We're assuming postgres, and the default connection settings are the same as postgres's defaults. Check `reference.conf`. These settings can be overridden by a file named `application.conf` on your classpath.


Migrations
---
* To start a new database migration, run `./new_migration.sh [project name] [schema name] [migration name]`. This will timestamp a new migration into the correct module directory, such as `./[project name]/src/main/resources/db/[schema name]/migration/<timestamp>__[migration name]` The migration name should not contain whitespace or `.sql`. That script will create the migration file and put you into an editor to complete it.
