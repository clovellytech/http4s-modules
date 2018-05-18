import sbt._

object dependencies {
  val addResolvers = Seq(
    "52north for postgis" at "http://52north.org/maven/repo/releases/",
    "jmcardon at bintray" at "https://dl.bintray.com/jmcardon/tsec",
    Resolver.sonatypeRepo("releases")
  )

  val exclusions = Seq(
    // see https://github.com/tpolecat/doobie/issues/568
    ExclusionRule("org.typelevel", "scala-library")
  )

  val compilerPlugins = Seq(
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
  )

  val bcrypt = "3.1"
  val cats = "1.1.0"
  val catsEffect = "0.10"
  val circe = "0.9.3"
  val cryptobits = "1.1"
  val doobie = "0.5.2"
  val flyway = "5.0.7"
  val fs2 = "0.10.3"
  val fs2cats = "0.5.0"
  val http4s = "0.18.9"
  val logback = "1.2.3"
  val pureConfig = "0.9.1"
  val scalaCheck = "1.13.5"
  val scalaTest = "3.0.5"
  val tsec = "0.0.1-M11"
  val typesafeConfig = "1.3.1"


  val httpDeps = Seq(
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-blaze-client",
    "org.http4s" %% "http4s-circe",
    "org.http4s" %% "http4s-dsl"
  ).map(_ % dependencies.http4s) ++ Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-java8"
  ).map(_ % dependencies.circe)

  val testDeps = Seq(
    "org.scalatest" %% "scalatest" % scalaTest,
    "org.tpolecat" %% "doobie-scalatest" % doobie,
    "org.scalacheck" %% "scalacheck" % scalaCheck
  ).map(_ % "test")

  val dbDeps = Seq(
    "org.flywaydb" % "flyway-core" % flyway,
    "org.postgresql" % "postgresql" % "42.2.2"
  ) ++ Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-postgres",
    "org.tpolecat" %% "doobie-hikari"
  ).map(_ % doobie)

  val commonDeps = Seq(
    "com.github.pureconfig" %% "pureconfig" % pureConfig,
    "ch.qos.logback" %  "logback-classic" % logback,
    "org.typelevel" %% "cats-core" % cats,
    "org.typelevel" %% "cats-effect" % catsEffect,
    "co.fs2" %% "fs2-core" % fs2,
    "co.fs2" %% "fs2-cats" % fs2cats,
    "co.fs2" %% "fs2-io" % fs2
  )

  val http4smodules = Seq(
    "com.clovellytech" %% "db" % "0.0.4-SNAPSHOT",
    "com.clovellytech" %% "auth" % "0.0.4-SNAPSHOT",
    "com.clovellytech" %% "features" % "0.0.4-SNAPSHOT"
  )
}

