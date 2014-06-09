package util

import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.{ISODateTimeFormat, DateTimeFormatter}

import scala.util.Try

/**
 * Mixin to add some implicits and a DateTime pimp for insuring that your trait's
 * local format and timezone are used through out the class.
 *
 * This also pimps the DateTimeFormatter to add some helpful Scala-friendly methods.
 */
trait DateTimeHelpers {

  import DateTimeHelpers._

  import language.implicitConversions

  /**
   * Copied from the helper default constructor of DateTime.
   *
   * This acts as a good extension point for defining our default parsing formatter.
   */
  def defaultParseFormat: DateTimeFormatter with Parsing = ISODateTimeFormat.dateTimeParser.withOffsetParsed

  /**
   * Copied from the DateTime.toString() method of DateTime.
   *
   * This acts as a good extension point for defining our default printing formatter.
   */
  def defaultPrintFormat: DateTimeFormatter with Printing = ISODateTimeFormat.dateTime

  /**
   * The default time zone on Rally.
   */
  def defaultDateTimeZone: DateTimeZone = DateTimeZone.UTC

  implicit def asParsingFormat(formatter: DateTimeFormatter): DateTimeFormatter with Parsing = {
    // fail fast when missing a parser
    require(formatter.getParser != null, s"Cannot use $formatter as a parsing formatter")
    formatter.asInstanceOf[DateTimeFormatter with Parsing]
  }

  implicit def asPrintingFormat(formatter: DateTimeFormatter): DateTimeFormatter with Printing = {
    // fail fast when missing a printer
    require(formatter.getPrinter != null, s"Cannot use $formatter as a printing formatter")
    formatter.asInstanceOf[DateTimeFormatter with Printing]
  }

  /**
   * Return the current time with the [[defaultDateTimeZone]].
   */
  def now: DateTime = new DateTime(defaultDateTimeZone)

  def parse(value: String): DateTime =
    DateTime.parse(value, defaultParseFormat) withZone defaultDateTimeZone

  def tryToParse(value: String): Try[DateTime] = Try(parse(value))

  def stringify(datetime: DateTime, printFormat: DateTimeFormatter = defaultPrintFormat): String =
    printFormat.print(datetime withZone defaultDateTimeZone)

  /**
   * Helpful implicit for ordering [[org.joda.time.DateTime]] from earliest to latest.
   */
  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan[DateTime](_ isBefore _)

}

object DateTimeHelpers extends DateTimeHelpers {

  // Marker traits for implicits

  trait Parsing {
    self: DateTimeFormatter =>
  }

  trait Printing {
    self: DateTimeFormatter =>
  }
}
