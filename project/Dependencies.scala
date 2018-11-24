import sbt._
import sbt.librarymanagement.DependencyBuilders

object dependencies {
  val addResolvers = Seq(
    "52north for postgis" at "http://52north.org/maven/repo/releases/",
    "jmcardon at bintray" at "https://dl.bintray.com/jmcardon/tsec",
    Resolver.sonatypeRepo("releases")
  )
  
  val compilerPlugins = Seq(
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )

  val bcrypt = "3.1"
  val cats = "1.4.0"
  val catsMtl = "0.4.0"
  val catsEffect = "1.0.0"
  val circe = "0.10.1"
  val cryptobits = "1.1"
  val doobie = "0.6.0"
  val flyway = "5.0.7"
  val fs2 = "1.0.0"
  val http4s = "0.20.0-M3"
  val logback = "1.2.3"
  val pureConfig = "0.9.2"
  val scalaCheck = "1.14.0"
  val scalaTest = "3.0.5"
  val simulacrum = "0.14.0"
  val tsec = "0.1.0-M1-SNAPSHOT"

  def orgVer(org : String, ver : String)(deps : String*) : Seq[ModuleID] = deps.map(org %% _ % ver)
  def org(org : String)(as : (String, String)*) : Seq[ModuleID] = as.map{ case (p, v) => org %% p % v }

  val httpDeps = orgVer("org.http4s", dependencies.http4s)(
    "http4s-blaze-server",
    "http4s-blaze-client",
    "http4s-circe",
    "http4s-dsl"
  ) ++ orgVer("io.circe", dependencies.circe)(
    "circe-core",
    "circe-generic",
    "circe-parser",
    "circe-java8"
  )

  val testDeps = Seq(
    "org.scalatest" %% "scalatest" % scalaTest,
    "org.tpolecat" %% "doobie-scalatest" % doobie,
    "org.scalacheck" %% "scalacheck" % scalaCheck
  )

  val testDepsInTestOnly = testDeps.map(_ % "test")

  val dbDeps = Seq(
    "org.flywaydb" % "flyway-core" % flyway,
    "org.postgresql" % "postgresql" % "42.2.2"
  ) ++ orgVer("org.tpolecat", doobie)(
    "doobie-core",
    "doobie-postgres",
    "doobie-hikari"
  )

  val commonDeps = org("org.typelevel")(
    "cats-core" -> cats,
    "cats-effect" -> catsEffect,
    "cats-mtl-core" -> catsMtl
  ) ++ Seq(
    "com.github.pureconfig" %% "pureconfig" % pureConfig,
    "ch.qos.logback" %  "logback-classic" % logback,
    "com.github.mpilquist" %% "simulacrum" % simulacrum
  ) ++ org("co.fs2")(
    "fs2-core" -> fs2,
    "fs2-io" -> fs2
  )

  val authDeps = orgVer("io.github.jmcardon", tsec)(
    "tsec-common",
    "tsec-password",
    "tsec-cipher-jca",
    "tsec-mac",
    "tsec-signatures",
    "tsec-hash-jca",
    "tsec-jwt-mac",
    "tsec-jwt-sig",
    "tsec-http4s"
  )
}
