import sbt._

object Build extends Build {

  val appName = "righttrack"
  val appVersion = "0.1"

  // Please keep these in alphabetical order
  val appDependencies = Seq(
    // TODO: Switch to SubCut?
    "com.google.inject" % "guice" % "3.0",
    "net.codingwell" % "scala-guice_2.10" % "4.0.0-beta",
    "com.h2database" % "h2" % "1.3.166",
    "com.typesafe" % "config" % "1.0.0",
    "com.typesafe.slick" %% "slick" % "1.0.1",
    "javax.inject" % "javax.inject" % "1",
    "org.clapper" % "classutil_2.10" % "1.0.2",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
//    "org.reactivemongo" %% "reactivemongo" % "0.9",
    "org.specs2" %% "specs2" % "2.0" % "test",
    "org.webjars" % "angularjs" % "1.1.5-1",
    "org.webjars" % "dojo" % "1.9.1",
    "org.webjars" % "requirejs" % "2.1.5",
    "org.webjars" % "webjars-play" % "2.1.0-1"
  )

  val main = play.Project(appName, appVersion, appDependencies)

}
