import sbt._

object Dependencies {

  object Versions {
    val cats       = "2.6.1"
    val catsEffect = "3.2.9"
    val chimney    = "0.6.1"

    val doobie      = "1.0.0-RC2"
    val doobieQuill = "0.0.5"

    val flyway = "8.0.4"

    val log4j    = "2.14.1"
    val log4cats = "2.2.0"
    val logback  = "1.2.7"

    val postgresql = "42.3.1"
    val pureconfig = "0.17.1"

    val scalatest = "3.2.10"

    val testcontainers = "1.16.3"

    val quill = "3.14.1"

    val weaver = "0.7.7"
  }

  val cats       = "org.typelevel" %% "cats-core"   % Versions.cats
  val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect
  val chimney    = "io.scalaland"  %% "chimney"     % Versions.chimney

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-postgres"
  ).map(_ % Versions.doobie)

  val doobieQuill = "org.polyvariant" %% "doobie-quill" % Versions.doobieQuill

  val quill = Seq(
    "io.getquill" %% "quill-core"
  ).map(_ % Versions.quill)

  val flyway = Seq(
    "org.flywaydb" % "flyway-core",
    "org.flywaydb" % "flyway-maven-plugin"
  ).map(_ % Versions.flyway)

  val log4j = "org.apache.logging.log4j" % "log4j-core" % Versions.log4j

  val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

  val postgresqlDriver = "org.postgresql" % "postgresql" % Versions.postgresql

  val log4cats = Seq(
    "org.typelevel" %% "log4cats-core",
    "org.typelevel" %% "log4cats-slf4j"
  ).map(_ % Versions.log4cats)

  val pureconfig = Seq(
    "com.github.pureconfig" %% "pureconfig",
    "com.github.pureconfig" %% "pureconfig-generic",
    "com.github.pureconfig" %% "pureconfig-cats-effect"
  ).map(_ % Versions.pureconfig)

  val testing = Seq(
    "com.disneystreaming" %% "weaver-core"    % Versions.weaver         % Test,
    "com.disneystreaming" %% "weaver-cats"    % Versions.weaver         % Test,
    "org.scalatest"       %% "scalatest"      % Versions.scalatest      % Test,
    "org.testcontainers"   % "testcontainers" % Versions.testcontainers % Test,
    "org.testcontainers"   % "postgresql"     % Versions.testcontainers % Test
  )
}
