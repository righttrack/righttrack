package database.dao

import models.activity.Activity
import scala.concurrent.Future
import models.{Version, EntityId}
import models.activity.verb.Verb
import database.CreateResult
import play.api.libs.iteratee.Enumerator

trait ActivityDAO {

  def record(activity: Activity): Future[CreateResult[Activity]]

  def findAll(limit: Int = 100): Enumerator[Activity]

}
