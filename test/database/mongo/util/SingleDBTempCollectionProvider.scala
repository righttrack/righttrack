package database.mongo.util

import reactivemongo.api._
import scala.annotation.tailrec
import scala.collection.concurrent
import scala.concurrent.ExecutionContext

/**
 * A test database provider for Mongo that has useful methods for testing and cleanup purposes.
 *
 * Currently, this interface enforces a single test database and connection and allows for generating
 * test collections with a consistent naming scheme that avoids name collision.
 *
 * @param ec An implicit execution context for initializing the database
 */
abstract class SingleDBTempCollectionProvider(generator: CollectionNameGenerator)(implicit ec: ExecutionContext)
  extends TempCollectionProvider {

  override type RegisteredCollection = this.TempCollection

  /**
   * The singleton test database name.
   *
   * @note this is final for reassurance that this is the only database name generated.
   */
  final val dbName: String = "test_righttrack"

  /**
   * The single connection for all test connections.
   */
  override val connection: MongoConnection

  /**
   * The singleton driver for all test connections with its own ActorSystem.
   *
   * @note made a val to indicate that this implementation is a singleton test DB provider.
   */
  override val driver: MongoDriver

  /**
   * The singleton database used for all provided test collections.
   *
   * @note this is a val for reassurance that there is only a single test database.
   */
  override protected lazy val db: DB = connection(dbName)

  /**
   * Adds a placeholder to the unique collection map with a unique name for the given commonName.
   *
   * @note this is useful for assigning a reference after a unique name has been generated for it.
   *
   * @param commonName the usual name for the collection
   * @param build a function to build a temp collection after the unique name has been registered
   * @param totalTries the number of times to attempt to register a collection name before throwing an exception.
   * @tparam C the type of temporary collection
   * @return the temporary collection
   * @throws RuntimeException when it cannot generate a unique name after the given number of totalTries
   */
  @throws[RuntimeException]
  final override protected def register[C <: RegisteredCollection](
    commonName: String,
    build: CollectionName with Unique => C,
    totalTries: Int = 3
  ): C = {
    @tailrec def attempt(attemptNum: Int): C = {
      if (attemptNum <= 0) {
        throw new RuntimeException(s"Could not register temporary collection '$commonName' after $totalTries tries.")
      }
      val uniqueAttempt = generator.nextUniqueName(commonName).asInstanceOf[CollectionName with Unique]
      collections.putIfAbsent(uniqueAttempt, null.asInstanceOf[C]) match {
        case Some(previous) =>
          // this attempt failed create a new closure that returns the value
          attempt(attemptNum - 1)
        case None =>
          val collection = build(uniqueAttempt)
          val prev = collections.replace(uniqueAttempt, collection)
          assert(
            prev == Some(null),
            s"Replaced non-placeholder value $prev while adding ${ uniqueAttempt.fullName }. This should never happen."
          )
          collection
      }
    }
    val collection = attempt(totalTries)
    onRegister(collection)
    collection
  }

  /**
   * Implements the allCollections method to allow for cleaning all collections.
   */
  override def allCollections: Iterable[TempCollection] = collections.values

  /**
   * A thread-safe map of collection names to the current set isolated test collection instance names.
   *
   * @return A map with a default value of an empty set of unique names for a given collection name.
   */
  private[this] val collections: concurrent.Map[CollectionName with Unique, TempCollection] =
    concurrent.TrieMap[CollectionName with Unique, TempCollection]()

  /**
   * Unregisters a collection from the collections map and calls onUnregister
   * @param name the unique name key of the collection
   */
  final protected def unregister(name: CollectionName with Unique): Unit = {
    collections.remove(name) match {
      case Some(collection) => onUnregister(collection)
      case None => // collection was already unregistered
    }
  }
}





