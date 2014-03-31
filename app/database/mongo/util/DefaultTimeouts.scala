package database.mongo.util

import scala.concurrent.duration._

private[mongo] trait DefaultTimeouts {

  val queryTimeout = FiniteDuration(5, SECONDS)
}
