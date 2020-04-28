h4sm - Scala Http4s Modules [![Join the chat][gitter-badge]][gitter-url] [![Build Status][travis-badge]][travis-url] [![Sonatype Release][sonatype-badge]][sonatype-url]
============

Create composable server modules with ease using Http4s, cats, Flyway, and PostgreSQL. Built for scala 2.12 and 2.13.

Basic Idea
---
To provide a library of production ready modules, complete with database schema, that can be composed and mounted as `HttpService`s. Modules included so far:

* auth - A complete user authentication implementation using `tsec`. Bearer and stateless cookie authentication schemes are available.
* features - An example library that allows users to submit feature requests and vote on features.
* files - A complete file upload and retrieval module. Only local file storage is implemented so far. AWS or other backends can be added by providing a typeclass instance.
* invitations - Add the ability for users to invite new users.
* messages - Allow authenticated users to pass messages to each other if they know the other's user id.
* permissions - Built on top of tsec-http4s, an easy way to create routes that are guarded by a user having certain permissions.
* petstore - As a learning example, the scala-pet-store implemented with h4sm modules (not released)
* store - A store with items and orders.
* more to come! Something you would like to see here? Submit an issue!

[See documentation here](https://clovellytech.github.io/http4s-modules)

Creating a module: [See a start to finish implementation example](https://clovellytech.github.io/http4s-modules/docs/by-example/petstore/)

## Contributors and Recognition

PRs and issues are so welcome on this project. Generally contributors don't have to be people that commit code or even write issues. Thanks especially to:

* [@pauljamescleary](https://github.com/pauljamescleary) for the [scala-pet-store](https://github.com/pauljamescleary/scala-pet-store) teaching project.
* [@estsauver](https://github.com/estsauver) for a pair coding session on this project when it was just starting.


Try it out
---
An example project exists in `/example-server`, which aggregates all the modules into a single server. To run, execute `exampleServerJVM/reStart` in sbt.

Get Started
---
Add any of the following dependencies to your build.sbt. There are interdependencies too, so you may consult `build.sbt` to see which modules depend on which, and thus may not need to be declared in your `build.sbt`.

```scala
libraryDependencies ++= Seq(
	"h4sm-auth",
	"h4sm-features",
	"h4sm-files",
	"h4sm-invitations",
	"h4sm-store",
	"h4sm-messages",
	"h4sm-permissions"
).map("com.clovellytech" %% _ % h4smVersion)
```

## Scalajs Clients

Clients are prebuilt for several modules. You can include them in your scalajs projects as:

```scala
libraryDependencies ++= Seq(
    "h4sm-auth-client",
    "h4sm-features-client",
).map("com.clovellytech" %%% _ % h4smVersion)
```


[gitter-badge]: https://badges.gitter.im/clovellytech/http4s-modules.svg "Chat"
[gitter-url]: https://gitter.im/clovellytech/http4s-modules?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge "Chat"
[travis-badge]: https://travis-ci.com/clovellytech/http4s-modules.svg?branch=master "Build Status"
[travis-url]: https://travis-ci.com/clovellytech/http4s-modules "Build Status"
[sonatype-badge]: https://img.shields.io/nexus/r/com.clovellytech/h4sm-auth_2.12.svg?server=https://oss.sonatype.org "Sonatype Releases"
[sonatype-url]: https://oss.sonatype.org/content/groups/public/com/clovellytech/ "Sonatype Releases"
