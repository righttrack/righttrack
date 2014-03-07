package database.dao

import database.CreateResult
import models.activity.Activity
import play.api.libs.iteratee.Enumerator
import scala.concurrent.Future

trait ActivityDAO {

  def record(activity: Activity): Future[CreateResult[Activity]]

  def findAll(limit: Int = 100): Enumerator[Activity]

}
