import dependencies._

val commonSettings = Seq(
  organization := "com.clovellytech",
  version := Version.version,
  scalaVersion := Version.scalaVersion,
  resolvers ++= addResolvers,
  scalacOptions ++= options.scalac,
  scalacOptions in (Compile, console) ~= (_.filterNot(options.badScalacConsoleFlags.contains(_)))
) ++ compilerPlugins

val withTests : String = "compile->compile;test->test"
val testOnly : String = "test->test"

lazy val db = (project in file("./db"))
  .settings(commonSettings)
  .settings(
    name := "h4sm-db",
    libraryDependencies ++= commonDeps ++ dbDeps ++ testDepsInTestOnly
  )

lazy val dbtesting = (project in file("./dbtesting"))
  .settings(commonSettings)
  .settings(
    name := "h4sm-dbtesting",
    libraryDependencies ++= commonDeps ++ dbDeps ++ testDeps
  )
  .dependsOn(db)

lazy val auth = (project in file("./auth"))
  .settings(commonSettings)
  .settings(
    name := "h4sm-auth",
    libraryDependencies ++= commonDeps ++ authDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(db)
  .dependsOn(dbtesting % "test->test")

lazy val files = (project in file("./files"))
  .settings(commonSettings)
  .settings(
    name := "h4sm-files",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(db % withTests, auth % withTests, dbtesting % withTests)

lazy val features = (project in file("./features"))
  .settings(commonSettings)
  .settings(
    name := "h4sm-features",
    mainClass in reStart := Some("h4sm.featurerequests.Server"),
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, dbtesting % testOnly)

lazy val permissions = (project in file("./permissions"))
  .settings(commonSettings)
  .settings(
    name := "h4sm-permissions",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, dbtesting % testOnly)

lazy val docs = (project in file("./docs"))
  .settings(name := "h4sm-docs")
  .enablePlugins(TutPlugin)
  .settings(commonSettings)
  .settings(
    name := "docs"
  )
  .dependsOn(auth, db, features, files)
  .dependsOn(dbtesting % "test->test")

lazy val h4sm = (project in file("."))
  .settings(name := "h4sm")
  .settings(commonSettings)
  .settings(skip in publish := true)
  .dependsOn(auth, db, files, features, permissions, dbtesting)
  .dependsOn(dbtesting % "test->test")
  .aggregate(auth, db, files, features, permissions, dbtesting)
