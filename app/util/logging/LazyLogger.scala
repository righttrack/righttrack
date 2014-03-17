package util.logging

import org.slf4j.{LoggerFactory, Logger}

trait HasLogger {

  def logger: Logger
}

trait LazyLogger extends HasLogger {

  lazy val logger: Logger = LoggerFactory.getLogger(getClass)
}
