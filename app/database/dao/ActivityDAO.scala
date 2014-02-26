package database.dao

import models.activity.Activity
import scala.concurrent.Future
import models.{Version, EntityId}
import models.activity.verb.Verb
import database.CreateResult

trait ActivityDAO {

  // TODO: Hide implementation based on BSON
  // Generic writer?

  def record[S <: EntityId, V <: Verb, O <: EntityId, VN <: Version](
    activity: Activity[S, V, O, VN]
  ): Future[CreateResult[Activity[S, V, O, VN]]]

}
