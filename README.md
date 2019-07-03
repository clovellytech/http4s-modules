h4sm - Scala Http4s Modules [![Join the chat][gitter-badge]][gitter-url] [![Build Status][travis-badge]][travis-url]
============


Create composable microservices or standalone servers with ease using Http4s, cats, Flyway, and PostgreSQL.

[See documentation here](https://clovellytech.github.io/http4s-modules)

Probably best place to start: [See a start to finish implementation example](https://clovellytech.github.io/http4s-modules/docs/by-example/petstore/)

Basic Idea
---
To provide a library of production ready modules, complete with database schema, that can be composed and mounted as `HttpService`s. Modules included so far:

* auth - A complete user authentication implementation using `tsec`. Only uses Bearer tokens for now.
* features - An example library that allows users to submit feature requests and vote on features.
* files - A complete file upload and retrieval module. Only local file storage is implemented so far. AWS or other backends can be added by providing a typeclass instance.
* invitations - Add the ability for users to invite new users.
* permissions - Built on top of tsec-http4s, an easy way to create routes that are guarded by a user having certain permissions.
* petstore - As a learning example, the scala-pet-store implemented with h4sm modules
* more to come! Something you would like to see here? Submit an issue! 

Contributors and Recognition
---
PRs and issues are so welcome on this project. Generally contributors don't have to be people that commit code or even write issues. Thanks especially to:

* [@pauljamescleary](https://github.com/pauljamescleary) for the [scala-pet-store](https://github.com/pauljamescleary/scala-pet-store) teaching project.
* [@estsauver](https://github.com/estsauver) for a pair coding session on this project when it was just starting.


Get Started
---
Add any of the following dependencies to your build.sbt:

```
libraryDependencies ++= Seq(
	"h4sm-auth",
	"h4sm-features",
	"h4sm-files",
	"h4sm-invitations",
	"h4sm-permissions"
).map("com.clovellytech" %% _ % "0.0.32")
```


[gitter-badge]: https://badges.gitter.im/clovellytech/http4s-modules.svg
[gitter-url]: https://gitter.im/clovellytech/http4s-modules?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge
[travis-badge]: https://travis-ci.com/clovellytech/http4s-modules.svg?branch=master
[travis-url]: https://travis-ci.com/clovellytech/http4s-modules