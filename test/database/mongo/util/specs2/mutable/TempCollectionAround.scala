package database.mongo.util.specs2.mutable

import database.util.HasMongoProvider
import org.specs2.execute.{Result, AsResult}
import org.specs2.mutable.Around
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.ExecutionContext

class TempCollectionAround(name: String)(implicit executionContext: ExecutionContext) extends Around {
  self: HasMongoProvider =>

  val collection: JSONCollection = self.mongo.tempJSONCollection(name)

  override def around[T : AsResult](body: => T): Result = {
    try AsResult(body)
    finally collection.drop()
  }
}
