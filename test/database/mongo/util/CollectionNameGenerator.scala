package database.mongo.util

/**
 * A simple object that generates names with the intent to be unique.
 *
 * It can be deterministic or psuedo-random, but it should avoid returning
 * the same name multiple times. This will slow down the generation of unique
 * collection names and can lead to a [[RuntimeException]].
 */
trait CollectionNameGenerator {

  def nextUniqueName(commonName: String): CollectionName
}

object CollectionNameGenerator {

  lazy val default: CollectionNameGenerator =
    fromSuffixGenerator(CollectionSuffixGenerator.default)

  implicit def fromFunction(function: String => CollectionName): CollectionNameGenerator =
    new Arbitrary(function)

  implicit def fromSuffixGenerator(generator: CollectionSuffixGenerator): CollectionNameGenerator =
    new UniqueSuffix(generator)

  /**
   * An arbitrary collection name generator that uses a function to create the name.
   *
   * It is arbitrary because it makes no commitment to retaining the common name.
   */
  class Arbitrary(val function: String => CollectionName) extends CollectionNameGenerator {

    final override def nextUniqueName(commonName: String): CollectionName = function(commonName)
  }

  /**
   * A name generator that always keeps the common name, but generates a unique suffix.
   */
  class UniqueSuffix(val generator: CollectionSuffixGenerator) extends CollectionNameGenerator {

    override def nextUniqueName(commonName: String): CollectionName =
      new CollectionName(commonName, generator.nextSuffix())
  }

}
