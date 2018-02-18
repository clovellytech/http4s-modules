import sbt._

object dependencies {
  val resolvers = Seq(
    "52north for postgis" at "http://52north.org/maven/repo/releases/",
    Resolver.sonatypeRepo("releases")
  )

  val exclusions = Seq(
    // see https://github.com/tpolecat/doobie/issues/568
    ExclusionRule("org.typelevel", "scala-library")
  )

  val plugins = Seq(
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
  )

  val flyway = "4.2.0"
  val pureConfig = "0.9.0"
  val http4s = "0.18.0"
  val circe = "0.9.1"
  val doobie = "0.5.0-RC1"
  val cats = "1.0.1"
  val catsEffect = "0.3"
  val scalaTest = "3.0.4"
  val fs2 = "0.9.7"
  val fs2cats = "0.3.0"
  val typesafeConfig = "1.3.1"
  val bcrypt = "3.1"
  val joda = "2.9.9"
  val cryptobits = "1.1"
  val logback = "1.2.3"
  val scalaCheck = "1.13.4"
}

