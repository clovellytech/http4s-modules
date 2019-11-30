import sbt._, Keys._
import sbt.librarymanagement.DependencyBuilders
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSCrossVersion
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.{crossProject => _, CrossType => _, _}
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._


object dependencies {
  val addResolvers = Seq(
    Resolver.sonatypeRepo("public")
  )

  object versions {
    val apacheLang3 = "3.9"
    val bcrypt = "3.1"
    val betterMonadicFor = "0.3.1"
    val cats = "2.0.0"
    val catsMtl = "0.7.0"
    val catsEffect = "2.0.0"
    val circe = "0.12.3"
    val circeConfig = "0.7.0"
    val cryptobits = "1.1"
    val doobie = "0.8.6"
    val flyway = "6.1.0"
    val http4s = "0.21.0-M6"
    val janino = "3.1.0"
    val kindProjector = "0.10.3"
    val logback = "1.2.3"
    val macroParadise = "2.1.1"
    val postgres = "42.2.8"
    val scalaCheck = "1.15.0-51107b8-SNAPSHOT"
    val scalajs = "0.9.7"
    val scalaJavaTime = "2.0.0-RC3"
    val scalaTest = "3.2.0-M2"
    val scalaTestPlusScalacheck = "3.1.0.0-RC2"
    val simulacrum = "1.0.0"
    val tsec = "0.2.0-M2"
  }

  val compilerPlugins = Seq(
    compilerPlugin("org.typelevel" %% "kind-projector" % versions.kindProjector),
    compilerPlugin("com.olegpy" %% "better-monadic-for" % versions.betterMonadicFor)
  )

  def compilerPluginsForVersion(version: String) =
    CrossVersion.partialVersion(version) match {
      case Some((2, major)) if major < 13 =>
        compilerPlugins ++ Seq(
          compilerPlugin("org.scalamacros" % "paradise" % versions.macroParadise cross CrossVersion.full),
        )
      case _ => compilerPlugins
    }

  val httpDeps = Seq(
    "http4s-server",
    "http4s-blaze-server",
    "http4s-blaze-client",
    "http4s-circe",
    "http4s-dsl"
  ).map("org.http4s" %% _ % versions.http4s)

  val testDeps = Seq(
    "org.scalatest" %% "scalatest" % versions.scalaTest,
    "org.scalatestplus" %% "scalatestplus-scalacheck" % versions.scalaTestPlusScalacheck,
    "org.tpolecat" %% "doobie-scalatest" % versions.doobie,
    "org.scalacheck" %% "scalacheck" % versions.scalaCheck
  )

  val testDepsInTestOnly = testDeps.map(_ % "test")

  val dbDeps = Seq(
    "org.flywaydb" % "flyway-core" % versions.flyway,
    "org.postgresql" % "postgresql" % versions.postgres
  ) ++ Seq(
    "doobie-core",
    "doobie-postgres",
    "doobie-hikari"
  ).map("org.tpolecat" %% _ % versions.doobie)

  val commonDeps = Seq(
    "cats-core" -> versions.cats,
    "cats-effect" -> versions.catsEffect,
    "cats-mtl-core" -> versions.catsMtl,
  ).map(("org.typelevel" %% (_: String) % (_: String)).tupled) ++ Seq(
    "org.apache.commons" % "commons-lang3" % versions.apacheLang3,
    "ch.qos.logback" %  "logback-classic" % versions.logback,
    "org.codehaus.janino" % "janino" % versions.janino,
    "org.typelevel" %% "simulacrum" % versions.simulacrum,
  ) ++ Seq(
    "circe-core",
    "circe-generic",
    "circe-parser",
//    "circe-java8"
  ).map("io.circe" %% _ % versions.circe) ++ Seq(
    "io.circe" %% "circe-config" % versions.circeConfig
  )

  val authDeps = Seq(
    "tsec-common",
    "tsec-password",
    "tsec-cipher-jca",
    "tsec-mac",
    "tsec-signatures",
    "tsec-hash-jca",
    "tsec-jwt-mac",
    "tsec-jwt-sig",
    "tsec-http4s"
  ).map("io.github.jmcardon" %% _ % versions.tsec)
}
