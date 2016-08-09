package models.activity

import models.activity.verb.Verb
import models.meta.{EntityTypes, EntityType}
import models.{AnyEntityId, EntityId, Entity}
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import serializers.{IdSerializer, DefaultSerializerFormats}

case class Activity(
  id: ActivityId,
  actorId: EntityId,
  verb: Verb,
  objectId: EntityId,
  created: DateTime = DateTime.now()
) extends Entity

object Activity extends DefaultSerializerFormats {

  implicit val activityFormat: OFormat[Activity] = {
    val format =
      (__ \ "id").format[ActivityId] and
      (__ \ "actor").format[AnyEntityId] and
      (__ \ "verb").format[String] and
      (__ \ "object").format[AnyEntityId] and
      (__ \ "timestamp").format[DateTime]
    format({
      case (id, actorId, verb, objectId, timestamp) =>
        Activity(id, actorId, Verb(verb), objectId, timestamp)
    }, {
      case Activity(id, EntityId(actorId, actorType), verb, EntityId(objectId, objectType), timestamp) =>
        (id, AnyEntityId(actorId, actorType), verb.action, AnyEntityId(objectId, objectType), timestamp)
    })
  }
}

case class ActivityId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.Activity
}

object ActivityId extends IdSerializer[ActivityId]
