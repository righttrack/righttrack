package serializers.api

import models._
import models.activity.Activity
import models.activity.ActivityId
import models.activity.verb.Verb
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import serializers._

trait ActivityIdSerializers extends EntityIdSerializers with StringEntityIdFormat {

  implicit lazy val activityIdFormat: Format[ActivityId] = Format id ActivityId
}

object ActivitySerializers extends Serializers with ActivityIdSerializers {

  implicit lazy val verbWriter: Format[Verb] = Json.format[Verb]

  implicit val activityFormat: Format[Activity] = {
    import TypedEntityIdFormat._
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
