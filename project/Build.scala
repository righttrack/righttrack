import sbt._
import sbt.Keys._

object Build extends Build {

  object Resolvers {
    val eligosourceReleases = "Eligosource Releases" at "http://repo.eligotech.com/nexus/content/repositories/eligosource-releases"
    val eligosourceSnapshots = "Eligosource Snapshots" at "http://repo.eligotech.com/nexus/content/repositories/eligosource-snapshots"
    val sonatypeReleases = "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
  }

  object Libraries {
    // dependency injection
    // TODO: Switch to SubCut?
    val guice = "com.google.inject" % "guice" % "3.0"
    val scalaGuide = "net.codingwell" % "scala-guice_2.10" % "4.0.0-beta"
    val inject = "javax.inject" % "javax.inject" % "1"

    val reactiveMongo = "org.reactivemongo" %% "reactivemongo" % "0.10.0"

    // slick
    val h2 = "com.h2database" % "h2" % "1.3.166"
    val config = "com.typesafe" % "config" % "1.0.0"
    val slick = "com.typesafe.slick" %% "slick" % "1.0.1"

    // event sourcing
//    val levelDB = "org.fusesource.leveldbjni" % "leveldbjni" % "1.8"
    val eventsourced = "org.eligosource" %% "eventsourced" % "0.6.0"
    val levelDBJournal = "org.eligosource" %% "eventsourced-journal-leveldb" % "0.6.0"

    // testing
    val mockito = "org.mockito" % "mockito-core" % "1.9.5" % "test"
    val specs2 = "org.specs2" %% "specs2" % "2.0" % "test"
    val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
  }

  object App {
    import Libraries._

    val name = "righttrack"
    val version = "0.1.0"

    val dependencies = Seq(
      config,
//      eventsourced,
      guice,
      h2,
      inject,
//      levelDBJournal,
      mockito,
      scalaCheck,
      scalaGuide,
      slick,
      specs2,
      reactiveMongo
    )
  }

  import Resolvers._

  val app = play.Project(App.name, App.version, App.dependencies)
    .settings(
      resolvers ++= Seq(
        eligosourceReleases,
        sonatypeReleases
      )
    )

}
                                                                                     Build