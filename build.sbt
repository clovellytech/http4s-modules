import dependencies._
import xerial.sbt.Sonatype._

val commonSettings = Seq(
  organization := "com.clovellytech",
  version := Version.version,
  scalaVersion := Version.scalaVersion,
  resolvers ++= addResolvers,
  scalacOptions ++= options.scalac,
  scalacOptions in (Compile, console) ~= (_.filterNot(options.badScalacConsoleFlags.contains(_))),
  updateOptions := updateOptions.value.withLatestSnapshots(false)
) ++ compilerPlugins


lazy val publishSettings = Seq(
  useGpg := true,
  publishMavenStyle := true,
  publishTo := sonatypePublishTo.value,
  publishArtifact in Test := false,
  homepage := Some(url("https://github.com/clovellytech/http4s-modules")),
  pomIncludeRepository := Function.const(false),
  sonatypeProfileName := "com.clovellytech",

  // License of your choice
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),

  // Where is the source code hosted
  sonatypeProjectHosting := Some(GitHubHosting("clovellytech", "http4s-modules", "pattersonzak@gmail.com"))
)

val withTests : String = "compile->compile;test->test"
val testOnly : String = "test->test"

lazy val db = (project in file("./db"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "h4sm-db",
    libraryDependencies ++= commonDeps ++ dbDeps ++ testDepsInTestOnly
  )

lazy val dbtesting = (project in file("./dbtesting"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "h4sm-dbtesting",
    libraryDependencies ++= commonDeps ++ httpDeps ++ dbDeps ++ testDeps
  )
  .dependsOn(db)

lazy val auth = (project in file("./auth"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-auth",
    libraryDependencies ++= commonDeps ++ authDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(db)
  .dependsOn(dbtesting % "test->test")

lazy val files = (project in file("./files"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-files",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(db % withTests, auth % withTests, dbtesting % withTests)

lazy val features = (project in file("./features"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-features",
    mainClass in reStart := Some("h4sm.featurerequests.Server"),
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, dbtesting % testOnly)

lazy val permissions = (project in file("./permissions"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-permissions",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, dbtesting % testOnly)

lazy val petstore = (project in file("./petstore"))
  .settings(commonSettings)
  .settings(commonSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-permissions",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, permissions, files, dbtesting % testOnly)

lazy val docs = (project in file("./h4sm-docs"))
  .settings(
    name := "h4sm-docs",
    moduleName := "h4sm-docs",
    mdocVariables := Map(
      "VERSION" -> version.value
    ),
    cancelable in Global := true
  )
  .settings(publishSettings)
  .settings(commonSettings)
  .settings(
    scalacOptions := options.consoleFlags
  )
  .enablePlugins(MdocPlugin)
  .enablePlugins(DocusaurusPlugin)
  .dependsOn(auth, db, dbtesting, features, files, permissions, petstore)

lazy val h4sm = (project in file("."))
  .settings(name := "h4sm")
  .settings(commonSettings)
  .settings(
    skip in publish := true,
    aggregate in reStart := false
  )
  .dependsOn(auth, db, files, features, permissions, dbtesting)
  .aggregate(auth, db, files, features, permissions, dbtesting)
