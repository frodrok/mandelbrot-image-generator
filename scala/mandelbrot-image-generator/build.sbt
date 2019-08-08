import Dependencies._

ThisBuild / scalaVersion     := "2.13.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.fhdev"
ThisBuild / organizationName := "fhdev"

lazy val server = (project in file("server"))
  .settings(
    name := "mandelbrot-generator-server",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.0-M5",
    libraryDependencies += "io.circe" %% "circe-core" % "0.12.0-M4",
    libraryDependencies += "io.circe" %% "circe-generic" % "0.12.0-M4",
    libraryDependencies += "io.circe" %% "circe-parser" % "0.12.0-M4",
    libraryDependencies += "org.typelevel" %% "spire" % "0.17.0-M1",
  //  libraryDependencies += "com.fhdev" %% "MandelbrotRequest" % "0.1.0-SNAPSHOT",
  ).dependsOn(protobuf)

lazy val client = (project in file("client"))
  .settings(
    name := "mandelbrot-generator-client",
    libraryDependencies += scalaTest % Test
  )

lazy val protobuf = (project in file("protobuf"))
  .settings(
    name := "protobuf-data",
    scalaVersion := "2.13.0",
    version := "0.1.0-SNAPSHOT"
  )
