name              := "multitest"
version           := "0.1"
scalaVersion      := "2.13.8"
scalafmtOnCompile := true

addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
addCompilerPlugin(("org.typelevel" % "kind-projector"     % "0.13.2").cross(CrossVersion.full))
testFrameworks += new TestFramework("weaver.framework.CatsEffect")

libraryDependencies += Dependencies.postgresqlDriver
libraryDependencies ++= Dependencies.flyway
libraryDependencies += Dependencies.log4j
libraryDependencies += Dependencies.logback
libraryDependencies ++= Dependencies.log4cats

libraryDependencies += Dependencies.cats
libraryDependencies += Dependencies.catsEffect
libraryDependencies += Dependencies.chimney

libraryDependencies ++= Dependencies.doobie
libraryDependencies += Dependencies.doobieQuill
libraryDependencies ++= Dependencies.quill

libraryDependencies ++= Dependencies.pureconfig

libraryDependencies ++= Dependencies.testing
