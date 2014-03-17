package database.mongo.util

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import scala.util.Random

/**
 * Generates a valid suffix for a Mongo collection name.
 */
trait CollectionSuffixGenerator {

  /**
   * Generate a meaningful or random nonce to use as a suffix to a Mongo collection name.
   *
   * @note the String must always be safe to use as a Mongo collection name.
   */
  def nextSuffix(): String
}

object CollectionSuffixGenerator {

  lazy val default = new TimeStamped() with RandomSuffixBits

  /**
   * Generates Mongo-safe collection names based on the generated timestamps.
   * @param pattern a safe suffix pattern to use for mongo collection name (not-validated).
   * @param nowProvider a function that produces the current timestamp.
   */
  class TimeStamped(
    val pattern: String = "yyyy-mm-dddd_hh-mm-ss",
    nowProvider: () => DateTime = () => new DateTime()
  ) extends CollectionSuffixGenerator {

    /**
     * Formats a datetime to be appended to collection names.
     *
     * @note this is a lazy val since Joda DateTimeFormatter is thread-safe and immutable and can be reused.
     */
    lazy val formatter: DateTimeFormatter = DateTimeFormat.forPattern(pattern)

    /**
     * Generates a new collection name suffix.
     */
    override def nextSuffix(): String = {
      val now = nowProvider()
      now.toString(formatter)
    }
  }

  /*
   * Mix-ins
   */

  /**
   * Adds some bit of randomness to the end of a collection name suffix.
   *
   * @note this is useful when the collection name generator is based on time.
   */
  trait RandomSuffixBits extends CollectionSuffixGenerator {

    def bits: Int = 4

    abstract override def nextSuffix(): String = {
      super.nextSuffix() + CollectionName.seperator + (Random.alphanumeric take bits mkString "")
    }
  }

}


