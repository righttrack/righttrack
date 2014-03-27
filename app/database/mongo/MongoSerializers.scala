package database.mongo

import models.common.CommonSerializers
import models.{WritesEntityId, EntityId, Serializers}
import play.api.libs.json._

trait MongoEntityIdSerializers extends WritesEntityId {
  self: Serializers =>

  implicit def entityIdWriter: Writes[EntityId] = Writes(it => Json.obj("_id" -> Json.obj("$oid" -> it.value)))

  implicit class ReadsObjectId(reads: Reads.type) {

    def id[T <: EntityId](from: String => T): Reads[T] = new Reads[T] {
      override def reads(json: JsValue): JsResult[T] = json \ "_id" match {
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

trait CommonMongoSerializers extends CommonSerializers with MongoEntityIdSerializers