package database.mongo.util

import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.{Future, ExecutionContext}
import scala.util.Success

/**
 * A Mongo DB collection provider that allows central control of all Mongo collections.
 *
 * This allows you to receive a collection without needing to worry about:
 *
 * 1. Naming conflicts
 * 2. Tracking all collections created including renamed collections
 * 3. Dropping any / all collections uniformly
 */
trait TempCollectionProvider {

  /**
   * The type of collection to register in the collections
   */
  type RegisteredCollection <: TempCollection

  /**
   * The single connection for all test connections.
   */
  protected def connection: MongoConnection

  /**
   * The singleton driver for all test connections with its own ActorSystem.
   *
   * @note made a val to indicate that this implementation is a singleton test DB provider.
   */
  protected def driver: MongoDriver

  /**
   * The singleton database used for all provided test collections.
   *
   * @note this is a val for reassurance that there is only a single test database.
   */
  protected def db: DB

  /**
   * Create a temporary JSONCollection that will get removed when the provider is cleaned up.
   *
   * @param commonName the name that the collection is usually known as
   * @return a collection with a unique name that is cleaned up when the provider is cleaned up
   */
  @inline final def tempJSONCollection(commonName: String): JSONCollection = register(commonName, buildJSONCollection)

  /**
   * Create a default test collection with a unique name, typically with the given name prefix.
   *
   * @param name the name of the collection.
   * @return a collection with the readable name and a unique suffix
   */
  protected def buildJSONCollection(name: CollectionName with Unique): JSONCollection with RegisteredCollection

  /**
   * Create a temporary BSONCollection that will get removed when the provider is cleaned up.
   *
   * @param commonName the name that the collection is usually known as
   * @return a collection with a unique name that is cleaned up when the provider is cleaned up
   */
  @inline final def tempBSONCollection(commonName: String): BSONCollection = register(commonName, buildBSONCollection)

  /**
   * Create a BSON test collection with a unique name, typically with the given name prefix.
   *
   * @param name the name of the collection.
   * @return a collection with the readable name and a unique suffix
   */
  protected def buildBSONCollection(name: CollectionName with Unique): BSONCollection with RegisteredCollection

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
  protected def register[C <: RegisteredCollection](
    commonName: String,
    build: CollectionName with Unique => C,
    totalTries: Int = 3
  ): C

  /**
   * A hook that is called exactly once when a new collection is registered.
   *
   * @param collection the registered collection
   */
  protected def onRegister(collection: RegisteredCollection): Unit = {}

  /**
   * Defines how to unregister a collection with its unique name.
   */
  protected def unregister(name: CollectionName with Unique): Unit

  /**
   * A hook that is called when a registered collection is unregistered.
   *
   * @param collection the registered collection
   */
  protected def onUnregister(collection: RegisteredCollection): Unit = {}

  /**
   * Access all registered collections.
   */
  def allCollections: Traversable[RegisteredCollection]

  /**
   * Drop all collections globally that have not already been dropped.
   */
  def dropAllCollections()(implicit ec: ExecutionContext): Unit = {
    for (collection <- allCollections) {
      collection.drop()
    }
  }

  /**
   * A convenience class owned by the [[TempCollectionProvider]].
   *
   * Typically subclasses will use this for their [[RegisteredCollection]] type.
   */
  trait TempCollection extends Collection with CollectionMetaCommands {
    parent =>

    /**
     * The unique name of this collection as provided.
     *
     * @note this does not inherit Unique since TempCollection is mutable and the name
     *       can stick around even after the collection has been dropped and the name
     *       reassigned. Only names that live inside the unique collections map are
     *       guaranteed to be Unique.
     */
    def uniqueName: CollectionName with Unique

    /**
     * Rename the collection by returning collection with the new name.
     */
    protected[this] def withName(newName: CollectionName with Unique): RegisteredCollection

    /**
     * Called before the provider calls drop on the collection.
     *
     * This is to distinguish from being called by the provider versus being called by the end user.
     */
    private[util] def beforeProviderDrop(): Unit = {}

    /**
     * Drops and unregisters the collection.
     *
     * @see [[CollectionMetaCommands.drop( )]]
     */
    abstract override def drop()(implicit ec: ExecutionContext): Future[Boolean] = {
      super.drop() andThen {
        case Success(true) => unregister(this.uniqueName)
      }
    }

    /**
     * Registers a new collection and drops the old collection if dropExisting = true.
     *
     * @see [[CollectionMetaCommands.rename( )]]
     */
    abstract override def rename(to: String, dropExisting: Boolean = false)
        (implicit ec: ExecutionContext): Future[Boolean] = {
      val newTempCollection: RegisteredCollection = register(to, this.withName)
      val rename = super.rename(newTempCollection.name, dropExisting)(ec)
      val renameAndUnregister = rename andThen {
        case Success(true) =>
        case renameFailed =>
          // remove the unsuccessful new mapping
          unregister(newTempCollection.uniqueName)
      }
      if (dropExisting) {
        renameAndUnregister andThen {
          case Success(true) =>
            // drop the original mapping for this
            unregister(this.uniqueName)
        }
      }
      else renameAndUnregister
    }
  }

}
