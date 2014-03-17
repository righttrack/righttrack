package database.mongo.util

import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api.{FailoverStrategy, MongoDriver, MongoConnection}
import scala.concurrent.ExecutionContext

class LocalhostTempCollectionProvider(generateName: CollectionNameGenerator)(implicit ec: ExecutionContext)
  extends SingleDBTempCollectionProvider(generateName) {

  def this()(implicit ec: ExecutionContext) = this(CollectionNameGenerator.default)

  override val driver: MongoDriver = new MongoDriver()
  override val connection: MongoConnection = driver.connection(List("localhost"))

  override protected def buildJSONCollection(
    collectionName: CollectionName with Unique
  ): JSONCollection with TempCollection =
    new TempJSONCollection(collectionName)

  class TempJSONCollection(collectionName: CollectionName with Unique)
    extends JSONCollection(db, collectionName.fullName, FailoverStrategy())
    with TempCollection {

    override val uniqueName: CollectionName with Unique = collectionName

    override protected[this]
    def withName(newName: CollectionName with Unique): TempJSONCollection =
      new TempJSONCollection(newName)
  }

  override protected def buildBSONCollection(
    collectionName: CollectionName with Unique
  ): BSONCollection with TempCollection =
    new TempBSONCollection(collectionName)

  class TempBSONCollection(collectionName: CollectionName with Unique)
    extends BSONCollection(db, collectionName.fullName, FailoverStrategy())
    with TempCollection {

    override val uniqueName: CollectionName with Unique = collectionName

    override protected[this]
    def withName(newName: CollectionName with Unique): TempBSONCollection =
      new TempBSONCollection(newName)
  }

}

object LocalhostTempCollectionProvider
  extends LocalhostTempCollectionProvider(CollectionSuffixGenerator.default)(ExecutionContext.global)