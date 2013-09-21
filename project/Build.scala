import sbt._

object Build extends Build {

  val appName = "righttrack"
  val appVersion = "0.1.0"

  // Please keep these in alphabetical order
  val appDependencies = Seq(
    // TODO: Switch to SubCut?
    "com.google.inject" % "guice" % "3.0",
    "net.codingwell" % "scala-guice_2.10" % "4.0.0-beta",
    "com.h2database" % "h2" % "1.3.166",
    "com.typesafe" % "config" % "1.0.0",
    "com.typesafe.slick" %% "slick" % "1.0.1",
    "javax.inject" % "javax.inject" % "1",
//    "org.clapper" % "classutil_2.10" % "1.0.2",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "org.specs2" %% "specs2" % "2.0" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies)

}
