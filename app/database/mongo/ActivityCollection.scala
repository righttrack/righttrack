package database.mongo

import database.dao.ActivityDAO
import models.activity.Activity
import play.api.libs.iteratee.Enumerator

class ActivityCollection(collection: BaseJsonCollection)
  extends BaseCollection
  with ActivityDAO {

  implicit val activityFormat: MongoJsonFormat[Activity] = MongoJsonFormat.fromOFormat[Activity]

  override def record(activity: Activity): Creates[Activity] =
    collection.createEntity(activity)

  override def findAll(limit: Int): Enumerator[Activity] =
    collection.find(MongoJson.obj()).cursor[Activity].enumerate(maxDocs = limit)
}
