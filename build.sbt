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
    libraryDependencies ++= commonDeps ++ dbDeps ++ testDeps
  )

lazy val auth = (project in file("./auth"))
  .settings(commonSettings)
  .settings(
    name := "auth",
    libraryDependencies ++= commonDeps ++ authDeps ++ dbDeps ++ httpDeps ++ testDeps
  )
  .dependsOn(db)

lazy val features = (project in file("./features"))
  .settings(commonSettings)
  .settings(
    name := "features",
    mainClass in reStart := Some("com.clovellytech.featurerequests.Server"),
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDeps
  )
  .dependsOn(auth, db)

lazy val docs = (project in file("./docs"))
  .settings(name := "features-docs")
  .enablePlugins(TutPlugin)
  .settings(commonSettings)
  .settings(
    name := "docs"
  )
  .dependsOn(auth, db, features)

lazy val featureRequests = (project in file("."))
  .settings(name := "feature-requests")
  .settings(commonSettings)
  .dependsOn(auth, db, features)
  .aggregate(auth, db, features)
