import dependencies._
import sbt._
import sbt.Keys._
import sbt.librarymanagement.DependencyBuilders
import sbtcrossproject.{CrossProject, Platform}
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSCrossVersion
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._

import sbtcrossproject.CrossPlugin.autoImport._

object ProjectImplicits {

  implicit class CommonSettings(val p: CrossProject) extends AnyVal {
    def commonSettings(): CrossProject = {
      p.settings(
        Seq(
          crossScalaVersions  := Seq(scala212, scala213),
          organization := "com.clovellytech",
          resolvers ++= addResolvers,
          // Make sure every subproject is using a logging configuration.
          Compile / unmanagedResourceDirectories ++= Seq((ThisBuild / baseDirectory).value / "shared/src/main/resources"),
          scalacOptions ++= options.scalacExtraOptionsForVersion(scalaVersion.value),
          libraryDependencies ++= compilerPluginsForVersion(scalaVersion.value),
        )
      )
    }
  }


  implicit class AddDependenciesOps(val p: CrossProject.Builder) extends AnyVal {
    def addScalaTest(): CrossProject = {
      p
      .jsConfigure(
        _.settings(
          libraryDependencies ++= Seq(
            "org.scalatest" %%% "scalatest" % versions.scalaTest % "test",
            "org.scalatestplus" %%% "scalatestplus-scalacheck" % versions.scalaTestPlusScalacheck % "test",
            "io.chrisdavenport" %%% "cats-scalacheck" % versions.catsScalacheck,
          )
        )
      )
      .jvmConfigure(
        _.settings(
          libraryDependencies ++= testDepsInTestOnly
        )
      )
    }
  }


  implicit class ConfigureJSProjectOps(val p: CrossProject.Builder) extends AnyVal {
    def configureJsProject(id: String): CrossProject = {
      p
      .configs(JsTest)
      .enablePlugins(ScalaJSPlugin)
      .enablePlugins(ScalaJSBundlerPlugin)
      .settings(
        scalacOptions += "-P:scalajs:sjsDefinedByDefault",
        libraryDependencies ++= Seq(
          "org.scalatest" %%% "scalatest" % versions.scalaTest % "test",
          "org.scalatestplus" %%% "scalatestplus-scalacheck" % versions.scalaTestPlusScalacheck % "test",
          "io.chrisdavenport" %%% "cats-scalacheck" % versions.catsScalacheck,
        ),
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
    }
  }
}
