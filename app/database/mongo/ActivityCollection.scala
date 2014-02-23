package database.mongo

import database.dao.ActivityDAO
import models.activity.Activity
import scala.concurrent.Future
import reactivemongo.api.collections.default.BSONCollection
import models.{Version, EntityId}
import reactivemongo.bson.{BSONInteger, BSONString, BSONDocument, BSONDocumentWriter}
import models.activity.verb.Verb
import database.CreateResult

class ActivityCollection(collection: BSONCollection)
  extends BaseCollection
  with ActivityDAO {

  override def record[S <: EntityId, V <: Verb, O <: EntityId, VN <: Version](
    activity: Activity[S, V, O, VN]
  ): Future[CreateResult[Activity[S, V, O, VN]]] = {
    val bson = BSONActivity.write(activity)
    collection.save(bson) map toCreateResult(activity)
  }
}

object BSONActivity {

  private[mongo] def writer[S <: EntityId, V <: Verb, O <: EntityId, VN <: Version](example: Activity[S, V, O, VN]) = {
    new BSONDocumentWriter[Activity[S, V, O, VN]] {
      override def write(activity: Activity[S, V, O, VN]): BSONDocument = write(activity)
    }
  }

  private[mongo] def write[S <: EntityId, V <: Verb, O <: EntityId, VN <: Version](activity: Activity[S, V, O, VN]) = {
    val idBson = BSONString(activity.id.value)
    val actorBson = BSONString(activity.actor.value)
    val verbBson = BSONString(activity.verb.action)
    val entityBson = BSONString(activity.entity.value)
    val versionBson = BSONInteger(activity.version.number)
    BSONDocument(Seq(
      "id" -> idBson,
      "actor" -> actorBson,
      "verb" -> verbBson,
      "entity" -> entityBson,
      "version" -> versionBson
    ))
  }
}
