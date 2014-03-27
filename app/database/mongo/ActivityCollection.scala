package database.mongo

import database.dao.ActivityDAO
import models.activity.Activity
import models.activity.ActivitySerializers._
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection

class ActivityCollection(collection: JSONCollection)
  extends BaseCollection
  with ActivityDAO {

  override def record(activity: Activity): Creates[Activity] =
    collection.insertResult(activity)

  override def findAll(limit: Int): Enumerator[Activity] =
    collection.find(Json.obj()).cursor[Activity].enumerate(maxDocs = limit)
}
