package database.mongo

import database.dao.ActivityDAO
import scala.concurrent.Future
import database.CreateResult
import play.api.libs.iteratee.Enumerator
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.{Json, JsObject}
import models.activity.Activity
import models.activity.ActivitySerializers._

class ActivityCollection(collection: JSONCollection)
  extends BaseCollection
  with ActivityDAO {

  override def record(activity: Activity): Future[CreateResult[Activity]] = {
    collection.save(activity) map toCreateResult(activity)
  }

  override def findAll(limit: Int): Enumerator[Activity] = {
    collection.find(Json.obj()).cursor[JsObject].enumerate().map(o =>
      Json.fromJson[Activity](o).get
    )
  }
}
