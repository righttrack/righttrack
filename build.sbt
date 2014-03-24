
name := "righttrack"

version := "0.1.1"

scalaVersion := "2.10.3"

resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
  // dependency injection
  "com.google.inject" % "guice" % "3.0",
  "net.codingwell" % "scala-guice_2.10" % "4.0.0-beta",
  "javax.inject" % "javax.inject" % "1",
  // mongo
  "org.reactivemongo" %% "reactivemongo" % "0.10.0",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",
  // slick
  "com.h2database" % "h2" % "1.3.166",
  "com.typesafe" % "config" % "1.0.0",
  "com.typesafe.slick" %% "slick" % "1.0.1",
  // testing
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.specs2" %% "specs2" % "2.3.8" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
)

play.Project.playScalaSettings

parallelExecution in Test := true

testOptions in Test += Tests.Argument("html")
