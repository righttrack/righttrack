package util.logging

import org.slf4j.helpers.NOPLogger
import org.slf4j.{LoggerFactory, Logger}
import play.api.{Mode, Play}

trait HasLogger {

  def logger: Logger
}

trait DefaultLogger extends ClassLogger with LogWhenLive with LazyLogger

trait ClassLogger extends HasLogger {

  override def logger: Logger = LoggerFactory.getLogger(getClass)
}

trait LogWhenLive extends HasLogger {

  private lazy val realLogger: Logger = LoggerFactory.getLogger(getClass)

  abstract override def logger: Logger = {
    Play.maybeApplication match {
      case Some(application) if application.mode != Mode.Test =>
        super.logger
      case otherwise =>
        val message =
          if (otherwise.isDefined) "Application in Test mode"
          else "No application is running"
        realLogger.debug(s"$message. Silencing all logs from ${getClass.getName}.")
        NOPLogger.NOP_LOGGER
    }
  }
}

trait LazyLogger extends HasLogger {

  abstract override lazy val logger: Logger = super.logger
}
