package database.mongo.util

import cake.GlobalExecutionContext
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import reactivemongo.api._
import scala.concurrent.{Future, Await}

class LocalhostTempCollectionProviderSpec
  extends Specification
  with GlobalExecutionContext
  with DefaultTimeouts
  with Mockito {

  val it = classOf[LocalhostTempCollectionProvider].getSimpleName
  val commonName = getClass.getSimpleName
  val mongo = new TestDBCollectionProvider

  // Exposes the db as a public property
  class TestDBCollectionProvider extends LocalhostTempCollectionProvider() {

    def underlyingDB: DB = db
  }

  abstract class TestInstanceCleaningLocalhostProvider(
    generateName: CollectionNameGenerator = CollectionSuffixGenerator.default
  ) extends LocalhostTempCollectionProvider(generateName) {

    class LocalTestCollection(val uniqueName: CollectionName with Unique)
      extends Collection
      with TempCollection {

      override val name: String = uniqueName.fullName
      override val failoverStrategy: FailoverStrategy = FailoverStrategy()
      override val db: DB = {
        val guard = spy(mongo.underlyingDB)
        // Always return success instead of going to the actual database
        doReturn(Future.successful(true)).when(guard).command(any, any)(any)
        guard
      }

      override protected[this]
      def withName(newName: CollectionName with Unique): RegisteredCollection =
        new LocalTestCollection(newName)
    }

  }

  it should {

    "not register the same name twice" in {
      object provider extends TestInstanceCleaningLocalhostProvider((_: String) => CollectionName(commonName, "same")) {
        val same = mock[TempCollection]

        def registerDuplicateName(): TempCollection =
          register[TempCollection](commonName, _ => same, totalTries = 3)
      }
      // when registering the first collection
      provider.registerDuplicateName() should be(provider.same)
      // when registering the same name again
      provider.registerDuplicateName() should throwAn[RuntimeException]
    }

    "register a new collection when renamed" in {
      var allRegisteredCollections = Set[CollectionName with Unique]()
      object provider extends TestInstanceCleaningLocalhostProvider() {

        def registerMock(): Collection with CollectionMetaCommands with TempCollection = {
          register(commonName, new LocalTestCollection(_))
        }

        override protected def onRegister(collection: TempCollection): Unit = {
          allRegisteredCollections += collection.uniqueName
        }

        override protected def onUnregister(collection: TempCollection): Unit = {
          allRegisteredCollections -= collection.uniqueName
        }
      }
      // generate a new mock
      val collection = provider.registerMock()
      allRegisteredCollections should have size 1
      // rename the collection
      val renameSuccess = Await.result(collection.rename(s"${ commonName }_renamed"), queryTimeout)
      assert(renameSuccess, s"Could not rename collection ${ collection.fullCollectionName }.")
      // since we didn't specify dropExisting, the old collection should remain registered
      allRegisteredCollections should have size 2
    }

    "unregister a renamed collection when dropExisting is true" in {
      var allRegisteredCollections = Set[CollectionName with Unique]()
      object provider extends TestInstanceCleaningLocalhostProvider() {

        def registerMock(): Collection with CollectionMetaCommands with TempCollection = {
          register(commonName, new LocalTestCollection(_))
        }

        override protected def onRegister(collection: TempCollection): Unit = {
          allRegisteredCollections += collection.uniqueName
        }

        override protected def onUnregister(collection: TempCollection): Unit = {
          allRegisteredCollections -= collection.uniqueName
        }
      }
      // generate a new mock
      val collection = provider.registerMock()
      allRegisteredCollections should have size 1
      // rename the collection
      val renameSuccess = Await.result(collection.rename(s"${ commonName }_renamed", dropExisting = true), queryTimeout)
      assert(renameSuccess, s"Could not rename collection ${ collection.fullCollectionName }.")
      // since we didn't specify dropExisting, the old collection should remain registered
      allRegisteredCollections should have size 1
    }
  }
}
