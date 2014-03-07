package models

import models.meta.EntityType
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.util.Failure
import scala.util.Success

/**
 * The base set of serializers.
 */
trait Serializers {

  implicit class ReadsId(reads: Reads.type) {
    def id[T <: EntityId](from: String => T) = new Reads[T] {
      override def reads(json: JsValue): JsResult[T] = json match {
        case JsString(value) => JsSuccess(from(value))
        case _ => JsError(
          "Entity id must be a String. For extracting a typed entity id, import " +
          s"${classOf[TypedEntityIdSerializers].getSimpleName} to read an ${classOf[AnyEntityId].getSimpleName}"
        )
      }
    }
  }

}

/**
 * A serializable entity id with no specific type.
 *
 * @param value the String value of the id
 * @param entityType the type of entity as tagged by this id
 */
case class AnyEntityId(value: String, entityType: EntityType) extends EntityId

trait TypedEntityIdSerializers {

  implicit val entityTypeFormat: Format[EntityType] = Format[EntityType](
    Reads[EntityType] {
      case JsString(idTypeName) => EntityType.tryFind(idTypeName) match {
        case Success(idType) => JsSuccess(idType)
        case Failure(error) => JsError(error.getMessage)
      }
      case js => JsError(s"Could not read EntityIdType from $js")
    },
    Writes[EntityType](idType => JsString(idType.name))
  )

  implicit val anyEntityIdFormat: Format[AnyEntityId] = {
    val format =
      (__ \ "id").format[String] and
      (__ \ "entityType").format[EntityType]
    format(AnyEntityId, id => (id.value, id.entityType))
  }

}

object TypedEntityIdSerializers extends TypedEntityIdSerializers