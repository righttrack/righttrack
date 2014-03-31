package controllers.api

import akka.util.Timeout
import scala.concurrent.duration._

private[controllers] trait DefaultTimeouts {

  implicit final def standard: Timeout = DefaultTimeouts.Implicits.standard

}

private[controllers] object DefaultTimeouts {

  object Implicits {

    implicit lazy val standard: Timeout = Timeout(5, MINUTES)
  }
}
