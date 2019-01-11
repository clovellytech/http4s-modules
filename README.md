h4sm - Scala Http4s Modules [![Join the chat][gitter-badge]][gitter-url] [![Build Status][travis-badge]][travis-url]
============


Create composable microservices or standalone servers with ease using Http4s, cats, Flyway, and PostgreSQL.

Basic Idea
---
To provide a library of production ready modules, complete with database schema, that can be composed and mounted as `HttpService`s. Modules included so far:

* auth - A complete user authentication implementation using `tsec`. Only uses Bearer tokens for now.
* features - An example library that allows users to submit feature requests and vote on features.
* files - A complete file upload and retrieval module. Only local file storage is implemented so far. AWS or other backends can be added by providing a typeclass instance.
* permissions - Built on top of tsec-http4s, an easy way to create routes that are guarded by a user having certain permissions.
* more to come! Something you would like to see here? Submit an issue! 

Contributors and Recognition
---
PRs and issues are so welcome on this project. Generally contributors don't have to be people that commit code or even write issues. Thanks especially to:

* [@pauljamescleary](https://github.com/pauljamescleary) for the [scala-pet-store](https://github.com/pauljamescleary/scala-pet-store) teaching project.
* [@estsauver](https://github.com/estsauver) for a pair coding session on this project when it was just starting.

Create your server!
---

See the example server in `/examples/votingapp`, for a simple demo server that can authenticate users and allow them to post feature requests and vote on features. Initializing the database schema is as simple as including the line:

```
      _ <- Stream.eval(h4sm.featurerequests.db.initializeAll(xa.kernel))
```

The feature requests module has a dependency on the `auth` module. This line causes the auth module schema to be migrated against the db, and then the feature requests module to be migrated.

Get Started
---
Add any of the following dependencies to your build.sbt:

```
libraryDependencies ++= Seq(
	"h4sm-auth",
	"h4sm-features",
	"h4sm-files", 
	"h4sm-permissions"
).map("com.clovellytech" %% _ % "0.0.13")
```

Setup
---
* We're assuming postgres, and the default connection settings are the same as postgres's defaults. Check `reference.conf`. These settings can be overridden by a file named `application.conf` on your classpath.
* We're depending on tsec with some version updates. Check script/travis_init.sh for some additional steps to clone a fork of tsec.
* We're depending on a fork of http4s with some version updates. Check script/travis_init.sh for some additional steps to clone a fork of http4s.

Migrations
---
* To start a new database migration, run `./new_migration.sh [project name] [schema name] [migration name]`. This will timestamp a new migration into the correct module directory, such as `./[project name]/src/main/resources/db/[schema name]/migration/<timestamp>__[migration name]` The migration name should not contain whitespace or `.sql`. That script will create the migration file and put you into an editor to complete it.


[gitter-badge]: https://badges.gitter.im/clovellytech/http4s-modules.svg
[gitter-url]: https://gitter.im/clovellytech/http4s-modules?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge
[travis-badge]: https://travis-ci.com/clovellytech/http4s-modules.svg?branch=master
[travis-url]: https://travis-ci.com/clovellytech/http4s-modules