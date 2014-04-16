package serializers.generic

import models.activity.Activity
import models.activity.ActivityId
import models.activity.verb.Verb
import models.{EntityId, AnyEntityId}
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import serializers._

trait ActivityIdSerializers extends Serializers {
  self: EntityIdFormat =>

  implicit lazy val activityIdFormat: Format[ActivityId] = Format id ActivityId
}

trait ActivitySerializers extends ActivityIdSerializers {
  self: EntityIdFormat =>

  implicit lazy val verbWriter: Format[Verb] = Json.format[Verb]

  implicit val activityFormat: Format[Activity] = {
    val format =
      (__ \ "id").format[ActivityId] and
      (__ \ "actor").format[AnyEntityId] and
      (__ \ "verb").format[String] and
      (__ \ "object").format[AnyEntityId] and
      (__ \ "timestamp").format[DateTime]
    format(
    {
      case (id, actorId, verb, objectId, timestamp) =>
        Activity(id, actorId, Verb(verb), objectId, timestamp)
    }, {
      case Activity(id, EntityId(actorId, actorType), verb, EntityId(objectId, objectType), timestamp) =>
        (id, AnyEntityId(actorId, actorType), verb.action, AnyEntityId(objectId, objectType), timestamp)
    }
    )
  }
}