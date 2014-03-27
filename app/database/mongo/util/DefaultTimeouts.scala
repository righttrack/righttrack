package database.mongo.util

import scala.concurrent.duration._

trait DefaultTimeouts {

  val queryTimeout = FiniteDuration(5, SECONDS)
}
