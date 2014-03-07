package models.activity

import models.activity.verb.Verb
import models.meta.{EntityTypes, EntityType}
import models.{EntityId, Entity}
import org.joda.time.DateTime

case class Activity(
  id: ActivityId,
  actorId: EntityId,
  verb: Verb,
  objectId: EntityId,
  time: DateTime = DateTime.now()
) extends Entity

case class ActivityId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.Activity
}