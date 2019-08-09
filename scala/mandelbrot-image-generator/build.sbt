import Dependencies._

ThisBuild / scalaVersion     := "2.13.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.fhdev"
ThisBuild / organizationName := "fhdev"

lazy val server = (project in file("server"))
  .settings(
    name := "mandelbrot-generator-server",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "io.circe" %% "circe-core" % "0.12.0-M4",
    libraryDependencies += "io.circe" %% "circe-generic" % "0.12.0-M4",
    libraryDependencies += "io.circe" %% "circe-parser" % "0.12.0-M4",
    libraryDependencies += "org.typelevel" %% "spire" % "0.17.0-M1",
  ).dependsOn(domainData)

lazy val client = (project in file("client"))
  .settings(
    name := "mandelbrot-generator-client",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "io.circe" %% "circe-core" % "0.12.0-M4",
    libraryDependencies += "io.circe" %% "circe-generic" % "0.12.0-M4",
    libraryDependencies += "io.circe" %% "circe-parser" % "0.12.0-M4",
    libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.28",
    libraryDependencies += "org.scalaz" %% "scalaz-effect" % "7.2.28",
    libraryDependencies += "org.scalaz" %% "scalaz-iteratee" % "7.2.28",
    libraryDependencies += "org.scalaz" %% "scalaz-concurrent" % "7.2.28",
  ).dependsOn(domainData)

// Contains JSON case classes
// and de-/serializers
lazy val domainData = (project in file("data"))
  .settings(
    name := "domain-data",
    scalaVersion := "2.13.0",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies += "io.circe" %% "circe-core" % "0.12.0-M4",
    libraryDependencies += "io.circe" %% "circe-generic" % "0.12.0-M4",
    libraryDependencies += "io.circe" %% "circe-parser" % "0.12.0-M4",
  )
