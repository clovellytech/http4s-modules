import dependencies._
import xerial.sbt.Sonatype._
import sbtcrossproject.{CrossProject, Platform}
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import ProjectImplicits._



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

lazy val db = crossProject(JVMPlatform)
  .in(file("./modules/db"))
  .jvmConfigure(_.configs(JvmTest))
  .commonSettings()
  .settings(
    name := "h4sm-db",
    libraryDependencies ++= commonDeps ++ dbDeps ++ testDepsInTestOnly
  )

lazy val testUtilCommon: CrossProject = crossProject(JVMPlatform, JSPlatform)
  .addScalaTest()
  .in(file("./modules/testutil/common"))
  .jsConfigure(_.configs(JsTest))
  .jvmConfigure(_.configs(JvmTest))
  .commonSettings()
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-testutil-common",
    libraryDependencies ++= commonDeps ++ testDeps
  )

lazy val testUtilDb = crossProject(JVMPlatform)
  .in(file("./modules/testutil/db"))
  .jvmConfigure(_.configs(JvmTest))
  .commonSettings()
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-testutil-db",
    libraryDependencies ++= commonDeps ++ httpDeps ++ dbDeps ++ testDeps
  )
  .dependsOn(testUtilCommon, db)

lazy val common = crossProject(JSPlatform)
  .configureJsProject("common")
  .in(file("./modules/common"))
  .commonSettings()
  .settings(
    name := "h4sm-common",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % versions.scalajs,
      "org.typelevel" %%% "simulacrum" % versions.simulacrum,
    ) ++ Seq(
      "circe-core",
      "circe-generic",
      "circe-parser",
    ).map("io.circe" %%% _ % versions.circe),
  )

lazy val authComm = crossProject(JSPlatform, JVMPlatform)
  .addScalaTest()
  .in(file("./modules/auth/comm"))
  .jvmConfigure(_.configs(JvmTest))
  .jsConfigure(_.configs(JsTest))
  .commonSettings()
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-auth-comm",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "simulacrum" % versions.simulacrum,
      "io.github.cquiroz" %%% "scala-java-time" % versions.scalaJavaTime,
    ) ++ Seq(
      "circe-core",
      "circe-generic",
      "circe-parser",
    ).map("io.circe" %%% _ % versions.circe)
  )
  .dependsOn(testUtilCommon % inTestOnly)

lazy val auth = crossProject(JVMPlatform)
  .in(file("./modules/auth/server"))
  .jvmConfigure(_.configs(JvmTest))
  .commonSettings()
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-auth",
    libraryDependencies ++= commonDeps ++ authDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(db)
  .dependsOn(testUtilDb % inTestOnly, testUtilCommon % inTestOnly)
  .dependsOn(authComm % withTests)

lazy val authClient = crossProject(JSPlatform)
  .configureJsProject("authClient")
  .commonSettings()
  .in(file("./modules/auth/client"))
  .settings(
    name := "h4sm-auth-client",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % versions.scalajs,
      "org.typelevel" %%% "simulacrum" % versions.simulacrum,
      "io.github.cquiroz" %%% "scala-java-time" % versions.scalaJavaTime
    ) ++ Seq(
      "circe-core",
      "circe-generic",
      "circe-parser",
    ).map("io.circe" %%% _ % versions.circe),
  )
  .dependsOn(common, authComm, testUtilCommon % withTests)

lazy val files = crossProject(JVMPlatform)
  .in(file("./modules/files"))
  .commonSettings()
  .jvmConfigure(_.configs(JvmTest))
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-files",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(db % withTests, auth % withTests, testUtilDb % withTests)

lazy val featuresServer = crossProject(JVMPlatform)
  .in(file("./modules/features/server"))
  .jvmConfigure(_.configs(JvmTest))
  .commonSettings()
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-features",
    mainClass in reStart := Some("h4sm.featurerequests.Server"),
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, featuresComm % withTests, testUtilDb % inTestOnly)

lazy val featuresComm = crossProject(JVMPlatform, JSPlatform)
  .addScalaTest()
  .in(file("./modules/features/comm"))
  .jvmConfigure(_.configs(JvmTest))
  .jsConfigure(_.configs(JsTest))
  .commonSettings()
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-features-comm",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "simulacrum" % versions.simulacrum,
    ) ++ Seq(
      "circe-core",
      "circe-generic",
      "circe-parser",
    ).map("io.circe" %%% _ % versions.circe)
  )
  .dependsOn(authComm % withTests)

lazy val featuresClient = crossProject(JSPlatform)
  .configureJsProject("featuresClient")
  .in(file("./modules/features/client"))
  .commonSettings()
  .settings(
    name := "h4sm-features-client",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % versions.scalajs,
      "org.typelevel" %%% "simulacrum" % versions.simulacrum,
    ) ++ Seq(
      "circe-core",
      "circe-generic",
      "circe-parser",
    ).map("io.circe" %%% _ % versions.circe)
  )
  .dependsOn(common, featuresComm % withTests, authClient, testUtilCommon % withTests)

lazy val permissions = crossProject(JVMPlatform)
  .in(file("./modules/permissions"))
  .jvmConfigure(_.configs(JvmTest))
  .commonSettings()
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-permissions",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, authComm % withTests, db % withTests, testUtilDb % withTests)


lazy val store = crossProject(JVMPlatform)
  .in(file("./modules/store"))
  .jvmConfigure(_.configs(JvmTest))
  .commonSettings()
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-store",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, permissions, files, testUtilDb % inTestOnly)

lazy val petstore = crossProject(JVMPlatform)
  .in(file("./modules/petstore"))
  .jvmConfigure(_.configs(JvmTest))
  .commonSettings()
  .settings(
    name := "h4sm-petstore",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, permissions, files, testUtilDb % inTestOnly)

lazy val invitations = crossProject(JVMPlatform)
  .in(file("./modules/invitations"))
  .jvmConfigure(_.configs(JvmTest))
  .commonSettings()
  .settings(publishArtifact in Test := true)
  .settings(
    name := "h4sm-invitations",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, testUtilDb % withTests)

lazy val messages = crossProject(JVMPlatform)
  .in(file("./modules/messages"))
  .commonSettings()
  .settings(
    name := "h4sm-messages",
    libraryDependencies ++= commonDeps ++ dbDeps ++ httpDeps ++ testDepsInTestOnly
  )
  .dependsOn(auth % withTests, db % withTests, testUtilDb % withTests)

lazy val docs = crossProject(JVMPlatform)
  .in(file("./h4sm-docs"))
  .commonSettings()
  .settings(
    name := "h4sm-docs",
    crossScalaVersions := Seq(scala212),
    moduleName := "h4sm-docs",
    mdocVariables := Map(
      "VERSION" -> version.value
    ),
    cancelable in Global := true,
    skip in publish := true,
  )
  .enablePlugins(MdocPlugin)
  .enablePlugins(DocusaurusPlugin)
  .dependsOn(auth, db, testUtilDb, testUtilCommon, featuresServer, files, permissions, petstore)

lazy val exampleServer = crossProject(JVMPlatform)
  .jvmConfigure(_.configs(JvmTest))
  .in(file("./example-server"))
  .settings(name := "example-server")
  .commonSettings()
  .settings(
    skip in publish := true,
    aggregate in reStart := false,
    libraryDependencies ++= commonDeps,
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(auth, db, files, featuresServer, featuresComm, permissions, store, testUtilCommon, testUtilDb, invitations)

lazy val root = crossProject(JVMPlatform)
  .in(file("."))
  .settings(
    name := "h4sm-root",
    skip in publish := true,
  )
  .aggregate(auth, db, files, featuresServer, permissions, store, testUtilCommon, testUtilDb, invitations)
