package models.activity

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.{TypedEntityIdSerializers, AnyEntityId, EntityId}
import models.activity.verb.Verb
import org.joda.time.DateTime

object ActivitySerializers extends TypedEntityIdSerializers {

  implicit lazy val verbWriter: Format[Verb] = Json.format[Verb]

  implicit val activityFormat: Format[Activity] = {
    val format =
      (__ \ "id").format[String] and
      (__ \ "actor").format[AnyEntityId] and
      (__ \ "verb").format[String] and
      (__ \ "object").format[AnyEntityId] and
      (__ \ "timestamp").format[DateTime]
    format({
      case (id, actorId, verb, objectId, timestamp) =>
        Activity(ActivityId(id), actorId, Verb(verb), objectId, timestamp)
    }, {
      case Activity(ActivityId(id), EntityId(actorId, actorType), verb, EntityId(objectId, objectType), timestamp) =>
        (id, AnyEntityId(actorId, actorType), verb.action, AnyEntityId(objectId, objectType), timestamp)
    })
  }
}
