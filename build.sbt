import play._

name := "righttrack"

version := "0.1.1"

scalaVersion := "2.11.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  // dependency injection
  "com.google.inject" % "guice" % "3.0",
  "net.codingwell" % "scala-guice_2.10" % "4.0.0-beta",
  "javax.inject" % "javax.inject" % "1",
  // mongo
  "org.reactivemongo" %% "reactivemongo" % "0.11.0-SNAPSHOT",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.0-SNAPSHOT",
  // play
  PlayImport.ws,
  // testing
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.specs2" %% "specs2" % "2.3.12" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
)

scalacOptions ++= Seq(
  "-g:vars",
  "-deprecation",
  "-feature",
  "-unchecked",
  "–optimise",
  "-Xfatal-warnings",
  "-Yinline",
  "-Yinline-warnings"
)

parallelExecution in Test := true

testOptions in Test += Tests.Argument("html")
