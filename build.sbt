import dependencies._

val commonSettings = Seq(
  organization := "com.clovellytech",
  version := Version.version,
  scalaVersion := Version.scalaVersion,
  resolvers ++= addResolvers,
  excludeDependencies ++= exclusions,
  scalacOptions ++= options.scalac,
  scalacOptions in (Compile, console) ~= (_.filterNot(options.badScalacConsoleFlags.contains(_)))
) ++ compilerPlugins


lazy val db = (project in file("./db"))
  .settings(commonSettings)
  .settings(
    name := "db",
    libraryDependencies ++= commonDeps ++ dbDeps ++ testDepsInTestOnly
  )

lazy val dbtesting = (project in file("./dbtesting"))
  .settings(commonSettings)
  .settings(
    name := "dbtesting",
    libraryDependencies ++= commonDeps ++ dbDeps ++ testDeps
  )
  .dependsOn(db)

lazy val auth = (project in file("./auth"))
  .settings(commonSettings)
  .settings(
    name := "auth",
    libraryDependencies ++= commonDeps ++ authDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(db)
  .dependsOn(dbtesting % "test->test")

lazy val features = (project in file("./features"))
  .settings(commonSettings)
  .settings(
    name := "features",
    mainClass in reStart := Some("com.clovellytech.featurerequests.Server"),
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth, db)
  .dependsOn(dbtesting % "test->test")

lazy val docs = (project in file("./docs"))
  .settings(name := "features-docs")
  .enablePlugins(TutPlugin)
  .settings(commonSettings)
  .settings(
    name := "docs"
  )
  .dependsOn(auth, db, features)
  .dependsOn(dbtesting % "test->test")

lazy val featureRequests = (project in file("."))
  .settings(name := "feature-requests")
  .settings(commonSettings)
  .dependsOn(auth, db, features)
  .dependsOn(dbtesting % "test->test")
  .aggregate(auth, db, features, dbtesting)
