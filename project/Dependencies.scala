import sbt._
import sbt.librarymanagement.DependencyBuilders

object dependencies {
  val addResolvers = Seq(
    Resolver.sonatypeRepo("public")
  )

  val apacheLang3 = "3.9"
  val bcrypt = "3.1"
  val betterMonadicFor = "0.3.1"
  val cats = "2.0.0"
  val catsMtl = "0.6.0"
  val catsEffect = "2.0.0"
  val circe = "0.12.0-RC4"
  val circeConfig = "0.7.0-M1"
  val cryptobits = "1.1"
  val doobie = "0.8.0-RC1"
  val flyway = "6.0.2"
  val http4s = "0.21.0-M4"
  val kindProjector = "0.10.3"
  val logback = "1.2.3"
  val macroParadise = "2.1.0"
  val postgres = "42.2.7"
  val scalaCheck = "1.14.0"
  val scalaTest = "3.0.8"
  val simulacrum = "0.19.0"
  val tsec = "0.2.0-M1"

  val compilerPlugins = Seq(
    compilerPlugin("org.typelevel" %% "kind-projector" % kindProjector),
    compilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicFor)
  )

  def compilerPluginsForVersion(version: String) =
    CrossVersion.partialVersion(version) match {
      case Some((2, major)) if major < 13 =>
        compilerPlugins ++ Seq(
          compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
        )
      case _ => compilerPlugins
    }

  val httpDeps = Seq(
    "http4s-server",
    "http4s-blaze-server",
    "http4s-blaze-client",
    "http4s-circe",
    "http4s-dsl"
  ).map("org.http4s" %% _ % http4s)

  val testDeps = Seq(
    "org.scalatest" %% "scalatest" % scalaTest,
    "org.tpolecat" %% "doobie-scalatest" % doobie,
    "org.scalacheck" %% "scalacheck" % scalaCheck
  )

  val testDepsInTestOnly = testDeps.map(_ % "test")

  val dbDeps = Seq(
    "org.flywaydb" % "flyway-core" % flyway,
    "org.postgresql" % "postgresql" % postgres
  ) ++ Seq(
    "doobie-core",
    "doobie-postgres",
    "doobie-hikari"
  ).map("org.tpolecat" %% _ % doobie)

  val commonDeps = Seq(
    "cats-core" -> cats,
    "cats-effect" -> catsEffect,
    "cats-mtl-core" -> catsMtl
  ).map(("org.typelevel" %% (_: String) % (_: String)).tupled) ++ Seq(
    "org.apache.commons" % "commons-lang3" % apacheLang3,
    "ch.qos.logback" %  "logback-classic" % logback,
    "com.github.mpilquist" %% "simulacrum" % simulacrum
  ) ++ Seq(
    "circe-core",
    "circe-generic",
    "circe-parser",
//    "circe-java8"
  ).map("io.circe" %% _ % circe) ++ Seq(
    "io.circe" %% "circe-config" % circeConfig
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
  ).map("io.github.jmcardon" %% _ % tsec)
}
