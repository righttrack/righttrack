package models

import models.meta.EntityType
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.util.Failure
import scala.util.Success

/**
 * The common base type of all Serializers.
 */
trait Serializers

/**
 * A trait for declaring that a set of Serializers defines a way to write a generic EntityId.
 */
trait WritesEntityId {
  self: Serializers =>

  implicit def entityIdWriter: Writes[EntityId]
}

/**
 * Provides implicit writer for ALL EntityId subclasses.
 *
 * @note This is incompatible with [[models.TypedEntityIdSerializers]] and cannot be extended
 *       or imported into the same scope or else you will get an implicit ambiguity compiler error.
 */
trait StringEntityIdSerializers extends WritesEntityId {
  self: Serializers =>

  /**
   * Writes the EntityId as a String.
   */
  implicit lazy val entityIdWriter: Writes[EntityId] =
    Writes[EntityId](id => JsString(id.value))

  implicit class ReadsStringId(reads: Reads.type) {

    def id[T <: EntityId](from: String => T): Reads[T] = new Reads[T] {
      override def reads(json: JsValue): JsResult[T] = json match {
        case JsString(value) => JsSuccess(from(value))
        case _ => JsError(
          "Entity id must be a String. For extracting a typed entity id, import " +
            s"${ classOf[TypedEntityIdSerializers].getSimpleName } to read an ${ classOf[AnyEntityId].getSimpleName }"
        )
      }
    }
  }

}

/**
 * Provides formats for [[models.meta.EntityType]] and [[models.AnyEntityId]].
 *
 * @note This is incompatible with [[models.StringEntityIdSerializers]] and cannot be extended
 *       or imported into the same scope or else you will get an implicit ambiguity compiler error.
 */
trait TypedEntityIdSerializers {
  self: Serializers =>

  implicit val entityTypeFormat: Format[EntityType] = Format[EntityType](
    Reads[EntityType] {
      case JsString(idTypeName) => EntityType.tryFind(idTypeName) match {
        case Success(idType) => JsSuccess(idType)
        case Failure(error) => JsError(error.getMessage)
      }
      case js => JsError(s"Could not read EntityIdType from $js")
    },
    Writes[EntityType](idType => JsString(idType.className))
  )

  implicit val anyEntityIdFormat: Format[AnyEntityId] = {
    val format =
      (__ \ "id").format[String] and
        (__ \ "entityType").format[EntityType]
    format(AnyEntityId, id => (id.value, id.entityType))
  }
}

object TypedEntityIdSerializers extends Serializers with TypedEntityIdSerializers