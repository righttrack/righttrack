package database.mongo

import models._
import models.common.CommonSerializers
import models.meta.EntityType
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.reflect.{classTag, ClassTag}

trait CommonMongoSerializers extends CommonSerializers with MongoEntityIdSerializers

trait MongoEntityIdSerializers extends WritesEntityId {
  self: Serializers =>

  implicit def entityIdWriter: Writes[EntityId] = MongoEntityIdSerializers.implEntityIdWriter

  implicit class ReadsMongoId(reads: Reads.type) {

    def id[T <: EntityId](from: String => T): Reads[T] = Reads { json =>
      json \ "_id" match {
        case JsUndefined() => JsError(s"No '_id' field in $json")
        case idObj => idObj \ "$oid" match {
          case JsUndefined() => JsError(
            s"Unrecognizable Entity id: must have an '$$oid' field to be an ObjectId from Mongo in $idObj"
          )
          case JsString(value) => JsSuccess(from(value))
          case weirdOID => JsError(
            s"Unrecognizable Entity id: '$$oid' field is not a String in $weirdOID"
          )
        }
      }
    }
  }
}

object MongoEntityIdSerializers extends Serializers with MongoEntityIdSerializers {

  private lazy val implEntityIdWriter = Writes[EntityId](it => Json.obj("_id" -> Json.obj("$oid" -> it.value)))
}

trait MongoTypedEntityIdSerializers extends WritesEntityId with FormatsAnyEntityId {
  self: Serializers =>

  override implicit lazy val entityIdWriter: Writes[EntityId] =
    MongoTypedEntityIdSerializers.implEntityIdWriter

  override implicit lazy val anyEntityIdFormat: Format[AnyEntityId] =
    MongoTypedEntityIdSerializers.implAnyEntityIdFormat

  implicit class ReadsTypedMongoId(reads: Reads.type) {

    def id[T <: EntityId : ClassTag](from: String => T): Reads[T] = {
      val ExpectedClass = classTag[T].runtimeClass.getName.intern()
      Reads { json =>
        anyEntityIdFormat.reads(json) flatMap {
          case AnyEntityId(value, entityType) =>
            if (entityType.className eq ExpectedClass) JsSuccess(from(value))
            else JsError(s"Wrong type of entity. Expected $ExpectedClass, read $entityType")
        }
      }
    }
  }

  implicit class FormatOps(formats: Format.type) {

    def id[T <: EntityId : ClassTag](from: String => T): Format[T] =
      Format(Reads id from, implicitly)

    def enum[T <: Enumeration](enum: T): Format[enum.Value] = Format(
      Reads {
        json => enum.values.find(_ == json.as[String]) match {
          case Some(value) => JsSuccess(value)
          case None => JsError(s"Unrecognized value for $enum enumeration.")
        }
      },
      Writes {
        it => JsString(it.toString)
      }
    )
  }
}

object MongoTypedEntityIdSerializers extends Serializers with MongoTypedEntityIdSerializers {

  import models.common.CommonSerializers.entityTypeFormat

  private[this] lazy val entityIdShape: OFormat[(String, EntityType)] =
    (__ \ "_id").format(
      (__ \ "$oid").format[String] and
      (__ \ "entityType").format[EntityType] tupled
    )

  private lazy val implEntityIdWriter: Writes[EntityId] = Writes {
    id => entityIdShape.writes(id.value, id.entityType)
  }

  private lazy val implAnyEntityIdFormat: Format[AnyEntityId] = Format(
    Reads {
      it => entityIdShape.reads(it) map {
        case (value, entityType) => AnyEntityId(value, entityType)
      }
    },
    implEntityIdWriter
  )
}