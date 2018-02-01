
val commonSettings = Seq(
  organization := "com.clovellytech",
  version := Version.version,
  scalaVersion := Version.scalaVersion,
  resolvers ++= dependencies.resolvers,
  excludeDependencies ++= dependencies.exclusions,
  scalacOptions ++= options.scalac,
  scalacOptions in (Compile, console) ~= (_.filterNot(options.badScalacConsoleFlags.contains(_)))
)


val httpDeps = Seq(
  "org.http4s" %% "http4s-blaze-server",
  "org.http4s" %% "http4s-blaze-client",
  "org.http4s" %% "http4s-circe",
  "org.http4s" %% "http4s-dsl"
).map(_ % dependencies.http4s) ++ Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % dependencies.circe)

val testDeps = Seq(
  "org.scalatest" %% "scalatest" % dependencies.scalaTest,
  "org.tpolecat" %% "doobie-scalatest" % dependencies.doobie
).map(_ % "test")

val dbDeps = Seq(
  "org.flywaydb" % "flyway-core" % dependencies.flyway,
  "org.postgresql" % "postgresql" % "42.1.4"
) ++ Seq(
  "org.tpolecat" %% "doobie-core",
  "org.tpolecat" %% "doobie-postgres",
  "org.tpolecat" %% "doobie-hikari",
).map(_ % dependencies.doobie)

val commonDeps = Seq(
  "com.github.pureconfig" %% "pureconfig" % dependencies.pureConfig,
  "ch.qos.logback" %  "logback-classic" % dependencies.logback,
  "org.typelevel" %% "cats-core" % dependencies.cats,
  "org.typelevel" %% "cats-effect" % dependencies.catsEffect,
  "co.fs2" %% "fs2-core" % dependencies.fs2,
  "co.fs2" %% "fs2-cats" % dependencies.fs2cats,
  "co.fs2" %% "fs2-io" % dependencies.fs2,
  "joda-time" % "joda-time" % dependencies.joda
)

lazy val db = (project in file("./db"))
  .settings(commonSettings)
  .settings(
    name := "db",
    libraryDependencies ++= commonDeps ++ dbDeps ++ testDeps
  )

lazy val featurerequests = (project in file("./features"))
  .settings(commonSettings)
  .settings(
    name := "features",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDeps
  )
  .dependsOn(db)

lazy val docs = (project in file("./docs"))
  .enablePlugins(TutPlugin)
  .settings(commonSettings)
  .settings(
    name := "docs"
  )
  .dependsOn(db, featurerequests)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .dependsOn(db, featurerequests)
  .aggregate(db, featurerequests)
