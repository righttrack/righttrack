package models

import models.meta.EntityType
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.util.Failure
import scala.util.Success
import scala.reflect.{classTag, ClassTag}

/**
 * The common base type of all Serializers.
 */
// TODO: Bound to serializer package
trait Serializers {

  implicit protected def postfixOk = scala.language.postfixOps
}

/**
 * A trait for declaring that a set of Serializers defines a way to write a generic EntityId.
 */
trait WritesEntityId {
  self: Serializers =>

  implicit def entityIdWriter: Writes[EntityId]
}

trait FormatsAnyEntityId {
  self: Serializers =>

  implicit def anyEntityIdFormat: Format[AnyEntityId]
}

/**
 * Provides implicit writer for ALL EntityId subclasses.
 *
 * @note This is incompatible with [[models.TypedEntityIdSerializers]] and cannot be extended
 *       or imported into the same scope or else you will get an implicit ambiguity compiler error.
 */
trait StringEntityIdSerializers extends WritesEntityId {
  self: Serializers =>

  implicit def entityIdWriter: Writes[EntityId] = StringEntityIdSerializers.implEntityIdWriter

  implicit class ReadsStringId(reads: Reads.type) {

    def id[T <: EntityId](from: String => T): Reads[T] = Reads {
      case JsString(value) => JsSuccess(from(value))
      case _ => JsError(
        "Entity id must be a String. For extracting a typed entity id, import " +
          s"${ classOf[TypedEntityIdSerializers].getSimpleName } to read an ${ classOf[AnyEntityId].getSimpleName }"
      )
    }
  }

  implicit class FormatStringId(formats: Format.type) {

    def id[T <: EntityId](from: String => T): Format[T] =
      Format(Reads id from, implicitly)
  }

}

object StringEntityIdSerializers extends Serializers with StringEntityIdSerializers {

  /**
   * Writes the EntityId as a String.
   */
  lazy val implEntityIdWriter: Writes[EntityId] = Writes[EntityId](id => JsString(id.value))
}

/**
 * Provides formats for [[models.meta.EntityType]] and [[models.AnyEntityId]].
 *
 * @note This is incompatible with [[models.StringEntityIdSerializers]] and cannot be extended
 *       or imported into the same scope or else you will get an implicit ambiguity compiler error.
 */
trait TypedEntityIdSerializers extends FormatsAnyEntityId with WritesEntityId {
  self: Serializers =>

  import models.common.CommonSerializers.entityTypeFormat

  private[this] lazy val anyEntityIdShape =
    (__ \ "id").format[String] and
    (__ \ "entityType").format[EntityType]


  override implicit lazy val anyEntityIdFormat: Format[AnyEntityId] = {
    anyEntityIdShape(AnyEntityId.apply, anyId => (anyId.value, anyId.entityType))
  }

  override implicit lazy val entityIdWriter: Writes[EntityId] = Writes {
    id => anyEntityIdFormat.writes(AnyEntityId(id.value, id.entityType))
  }

  implicit class ReadTypedEntityId(reads: Reads.type) {

    def id[T <: EntityId : ClassTag](from: String => T): Reads[T] = Reads {
      json =>
        val ExpectedClassName = classTag[T].runtimeClass.getName.intern()
        anyEntityIdFormat.reads(json).flatMap(anyId => {
          anyId.entityType.className match {
            case ExpectedClassName => JsSuccess(from(anyId.value))
            case unexpected => JsError(s"Unexpected entity id type: $unexpected")
          }
        })
    }
  }

  implicit class FormatTypedEntityId(formats: Format.type) {

    def id[T <: EntityId : ClassTag](from: String => T): Format[T] =
      Format(Reads id from, entityIdWriter)
  }
}

object TypedEntityIdSerializers extends Serializers with TypedEntityIdSerializers