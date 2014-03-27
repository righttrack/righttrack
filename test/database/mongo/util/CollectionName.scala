package database.mongo.util

import reactivemongo.api.Collection

/**
 * A two part name for a Mongo collection.
 *
 * @param commonName the common name for this collection (ie. the name of the real collection)
 * @param suffix a disambiguating suffix
 */
case class CollectionName(commonName: String, suffix: String) {

  def this(collection: Collection, suffix: String) = this(collection.name, suffix)

  lazy val fullName: String = commonName + CollectionName.seperator + suffix
}

object CollectionName {

  @inline final val seperator = "_"
}

/**
 * Marker trait for making TempCollectionNames compile-time checked for uniqueness.
 */
sealed trait Unique {
  self: CollectionName =>
}