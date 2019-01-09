import sbt._
import sbt.librarymanagement.DependencyBuilders

object dependencies {
  val addResolvers = Seq(
    "52north for postgis" at "http://52north.org/maven/repo/releases/",
    "jmcardon at bintray" at "https://dl.bintray.com/jmcardon/tsec",
    Resolver.sonatypeRepo("releases")
  )
  
  val compilerPlugins = Seq(
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )

  val bcrypt = "3.1"
  val cats = "1.5.0"
  val catsMtl = "0.4.0"
  val catsEffect = "1.1.0"
  val circe = "0.11.0"
  val circeConfig = "0.6.0"
  val cryptobits = "1.1"
  val doobie = "0.6.0"
  val flyway = "5.2.4"
  val http4s = "0.20.0-M5-ct"
  val logback = "1.2.3"
  val postgres = "42.2.5"
  val scalaCheck = "1.14.0"
  val scalaTest = "3.0.5"
  val simulacrum = "0.14.0"
  val tsec = "0.1.1-ct"

  val httpDeps = Seq(
    "http4s-server",
    "http4s-blaze-server",
    "http4s-blaze-client",
    "http4s-circe",
    "http4s-dsl"
  ).map("org.http4s" %% _ % http4s) ++ Seq(
    "circe-core",
    "circe-generic",
    "circe-parser",
    "circe-java8"
  ).map("io.circe" %% _ % circe) ++ Seq(
    "io.circe" %% "circe-config" % circeConfig
  )

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
  ).map(("org.typelevel" %% (_ : String) % (_: String)).tupled) ++ Seq(
    "ch.qos.logback" %  "logback-classic" % logback,
    "com.github.mpilquist" %% "simulacrum" % simulacrum
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
