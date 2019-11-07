import dependencies._
import xerial.sbt.Sonatype._
import sbtcrossproject.CrossProject
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

inThisBuild(
  Seq(
    organization := "com.clovellytech",
    homepage := Some(url("https://github.com/clovellytech/http4s-modules")),
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    developers := List(
      Developer(
        "zakpatterson",
        "Zak Patterson",
        "pattersonzak@gmail.com",
        url("https://github.com/zakpatterson")
      )
    )
  )
)


val scala212 = "2.12.9"
val scala213 = "2.13.0"

lazy val JsTest = config("js").extend(Test)
lazy val JvmTest = config("jvm").extend(Test)

val commonSettings = Seq(
  crossScalaVersions  := Seq(scala212, scala213),
  organization := "com.clovellytech",
  scalaVersion := Version.scalaVersion,
  resolvers ++= addResolvers,
  scalacOptions ++= options.scalacOptionsForVersion(scalaVersion.value),
  scalacOptions in (Compile, console) ~= (_.diff(options.badScalacConsoleFlags)),
  scalacOptions in (Test, console) ~= (_.diff(options.badScalacConsoleFlags)),
  updateOptions := updateOptions.value.withLatestSnapshots(false),
  libraryDependencies ++= compilerPluginsForVersion(scalaVersion.value),
)

lazy val copyFastOptJS = TaskKey[Unit]("copyFastOptJS", "Copy javascript files to target directory")


def jsProject(id: String, in: String): CrossProject = CrossProject(id, file(in))(JSPlatform)
  .configs(JsTest)
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(commonSettings)
  .settings(
    name := id,
    scalacOptions += "-P:scalajs:sjsDefinedByDefault",
    useYarn := true, // makes scalajs-bundler use yarn instead of npm
    requireJsDomEnv in Test := true,
    scalaJSUseMainModuleInitializer := true,
    // configure Scala.js to emit a JavaScript module instead of a top-level script
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
    // https://scalacenter.github.io/scalajs-bundler/cookbook.html#performance
    webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),
    copyFastOptJS := {
      val inDir = (crossTarget in (Compile, fastOptJS)).value
      val outDir = (baseDirectory in (Compile, fastOptJS)).value / ".." / "static/public" / id
      val fileNames = Seq(
        s"${name.value}-fastopt-loader.js",
        s"${name.value}-fastopt-library.js",
        s"${name.value}-fastopt-library.js.map",
        s"${name.value}-fastopt.js",
        s"${name.value}-fastopt.js.map",
      )
      val copies = fileNames.map(p => (inDir / p, outDir / p))
      IO.copy(copies, overwrite = true, preserveLastModified = true, preserveExecutable = true)
    },
    // hot reloading configuration:
    // https://github.com/scalacenter/scalajs-bundler/issues/180
    addCommandAlias("dev", "; compile; fastOptJS::startWebpackDevServer; devwatch; fastOptJS::stopWebpackDevServer"),
    addCommandAlias("devwatch", "~; compile; fastOptJS; copyFastOptJS"),
  )


val withTests : String = "compile->compile;test->test"
val testOnly : String = "test->test"

lazy val db = crossProject(JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./modules/db"))
  .settings(commonSettings)
  .settings(
    name := "h4sm-db",
    libraryDependencies ++= commonDeps ++ dbDeps ++ testDepsInTestOnly
  )

lazy val testUtilCommon = crossProject(JVMPlatform, JSPlatform)
  .jsConfigure(_.configs(JsTest))
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./modules/testutil/common"))
  .settings(commonSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-testutil-common",
    libraryDependencies ++= commonDeps ++ testDeps
  )

lazy val testUtilDb = crossProject(JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./modules/testutil/db"))
  .settings(commonSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-testutil-db",
    libraryDependencies ++= commonDeps ++ httpDeps ++ dbDeps ++ testDeps
  )
  .dependsOn(testUtilCommon, db)

lazy val common = jsProject("common", "./modules/common")
  .settings(name := "h4sm-common")
  .settings(commonSettings)
  .settings(
    skip in publish := true,
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % versions.scalaTest % "test",
      "org.scalatestplus" %%% "scalatestplus-scalacheck" % versions.scalaTestPlusScalacheck % "test",
      "org.scala-js" %%% "scalajs-dom" % versions.scalajs,
      "org.typelevel" %%% "simulacrum" % versions.simulacrum,
    ) ++ Seq(
      "circe-core",
      "circe-generic",
      "circe-parser",
    ).map("io.circe" %%% _ % versions.circe) ++ Seq(
      "io.circe" %%% "not-java-time" % versions.notJavaTime,
    ) ++ testDeps.map(_ % "test"),
  )

lazy val authComm = crossProject(JSPlatform, JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .jsConfigure(_.configs(JsTest))
  .in(file("./modules/auth/comm"))
  .settings(commonSettings)
  .settings(
    name := "h4sm-shared",
    skip in publish := true,
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % versions.scalaTest % "test",
      "org.scalatestplus" %%% "scalatestplus-scalacheck" % versions.scalaTestPlusScalacheck % "test",
      "org.typelevel" %%% "simulacrum" % versions.simulacrum,
      "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-RC3"
    ) ++ Seq(
      "circe-core",
      "circe-generic",
      "circe-parser",
    ).map("io.circe" %%% _ % versions.circe) ++ testDeps.map(_ % "test")
  )

lazy val auth = crossProject(JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./modules/auth/server"))
  .settings(commonSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-auth",
    libraryDependencies ++= commonDeps ++ authDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(db)
  .dependsOn(testUtilDb % testOnly)
  .dependsOn(authComm)

lazy val authClient = jsProject("authClient", "./modules/auth/client")
  .settings(name := "auth-client")
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % versions.scalaTest % "test",
      "org.scalatestplus" %%% "scalatestplus-scalacheck" % versions.scalaTestPlusScalacheck % "test",
      "org.scala-js" %%% "scalajs-dom" % versions.scalajs,
      "org.typelevel" %%% "simulacrum" % versions.simulacrum,
      "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-RC3"
    ) ++ Seq(
      "circe-core",
      "circe-generic",
      "circe-parser",
    ).map("io.circe" %%% _ % versions.circe) ++ Seq(
      "io.circe" %%% "not-java-time" % versions.notJavaTime,
    ) ++ testDeps.map(_ % "test"),
  )
  .dependsOn(common, authComm, testUtilCommon % withTests)

lazy val files = crossProject(JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./modules/files"))
  .settings(commonSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-files",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(db % withTests, auth % withTests, testUtilDb % withTests)

lazy val features = crossProject(JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./modules/features"))
  .settings(commonSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-features",
    mainClass in reStart := Some("h4sm.featurerequests.Server"),
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, testUtilDb % testOnly)

lazy val permissions = crossProject(JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./modules/permissions"))
  .settings(commonSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-permissions",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, authComm, db % withTests, testUtilDb % withTests)


lazy val store = crossProject(JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./modules/store"))
  .settings(commonSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-store",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, permissions, files, testUtilDb % testOnly)

lazy val petstore = crossProject(JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./modules/petstore"))
  .settings(commonSettings)
  .settings(
    name := "h4sm-petstore",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, permissions, files, testUtilDb % testOnly)

lazy val invitations = crossProject(JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./modules/invitations"))
  .settings(commonSettings)
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-invitations",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, testUtilDb % withTests)

lazy val docs = crossProject(JVMPlatform)
  .in(file("./h4sm-docs"))
  .settings(
    name := "h4sm-docs",
    crossScalaVersions := Seq(scala212),
    moduleName := "h4sm-docs",
    mdocVariables := Map(
      "VERSION" -> version.value
    ),
    cancelable in Global := true
  )
  .settings(commonSettings)
  .enablePlugins(MdocPlugin)
  .enablePlugins(DocusaurusPlugin)
  .dependsOn(auth, db, testUtilDb, features, files, permissions, petstore)

lazy val exampleServer = crossProject(JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./example-server"))
  .settings(name := "example-server")
  .settings(commonSettings)
  .settings(
    skip in publish := true,
    aggregate in reStart := false,
    libraryDependencies ++= commonDeps,
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(auth, db, files, features, permissions, store, testUtilCommon, testUtilDb, invitations)

lazy val root = crossProject(JVMPlatform)
  .in(file("."))
  .settings(
    name := "h4sm-root",
    skip in publish := true,
  )
  .aggregate(auth, db, files, features, permissions, store, testUtilCommon, testUtilDb, invitations)
