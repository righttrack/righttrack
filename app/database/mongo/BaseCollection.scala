package database.mongo

import database.dao.BaseDAO
import models.Entity
import play.api.libs.json.Writes
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.{Future, ExecutionContext}

trait BaseCollection extends BaseDAO with BaseCollection.Implicits {

  override type CreateResultType[T <: Entity] = MongoCreateResult[T]
}

object BaseCollection {

  trait Implicits {

    implicit protected def executionContext: ExecutionContext = ExecutionContext.global

    implicit class JSONCollectionOps(collection: JSONCollection) {

      def insertResult[T <: Entity : Writes](entity: T): Future[MongoCreateResult[T]] = {
        val entityObj = implicitly[Writes[T]].writes(entity)
        collection.insert(entityObj) map MongoCreateResult(entity)
      }
    }

  }

  object Implicits extends Implicits

}

