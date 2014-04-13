package serializers

import models.meta.EntityType
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.reflect.{classTag, ClassTag}
import models.{EntityId, AnyEntityId}

/**
 * The common base type of all Serializers.
 */
// TODO: Bound to serializer package
trait Serializers {

  implicit protected def postfixOk = scala.language.postfixOps

  implicit class FormatOps(format: Format.type) {

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

// TODO: Document
trait EntityIdSerializers extends Serializers {
  self: EntityIdFormat =>

}

// TODO: Document
sealed trait EntityIdFormat {
  self: EntityIdSerializers =>
}

sealed trait ReadsIdOps {

  def id[T <: EntityId : ClassTag](from: String => T): Reads[T]
}

sealed class FormatsIdOps(implicit op: Reads.type => ReadsIdOps, entityIdWriter: Writes[EntityId]) {

  def id[T <: EntityId : ClassTag](from: String => T): Format[T] = Format(Reads id from, entityIdWriter)
}

/**
 * A trait for declaring that a set of Serializers defines a way to write a generic EntityId.
 */
trait WritesEntityId {
  self: EntityIdFormat =>

  implicit def entityIdWriter: Writes[EntityId]
}

trait FormatsAnyEntityId {
  self: EntityIdFormat =>

  implicit def anyEntityIdFormat: Format[AnyEntityId]
}

/**
 * Provides implicit writer for ALL EntityId subclasses.
 *
 * @note This is incompatible with [[serializers.TypedEntityIdFormat]] and cannot be extended
 *       or imported into the same scope or else you will get an implicit ambiguity compiler error.
 */
trait StringEntityIdFormat extends EntityIdFormat with WritesEntityId {
  self: EntityIdSerializers =>

  /**
   * Writes the EntityId as a String.
   */
  override implicit lazy val entityIdWriter: Writes[EntityId] = Writes[EntityId](id => JsString(id.value))

  implicit class ReadsStringId(reads: Reads.type) extends ReadsIdOps {

    override def id[T <: EntityId : ClassTag](from: String => T): Reads[T] = Reads {
      case JsString(value) => JsSuccess(from(value))
      case _ => JsError(
        "Entity id must be a String. For extracting a typed entity id, import " +
          s"${ classOf[TypedEntityIdFormat].getSimpleName } to read an ${ classOf[AnyEntityId].getSimpleName }"
      )
    }
  }

  implicit class FormatsStringId(format: Format.type) extends FormatsIdOps

}

object StringEntityIdFormat extends EntityIdSerializers with StringEntityIdFormat

/**
 * Provides formats for [[models.meta.EntityType]] and [[models.AnyEntityId]].
 *
 * @note This is incompatible with [[serializers.StringEntityIdFormat]] and cannot be extended
 *       or imported into the same scope or else you will get an implicit ambiguity compiler error.
 */
trait TypedEntityIdFormat extends EntityIdFormat with FormatsAnyEntityId with WritesEntityId {
  self: EntityIdSerializers =>

  import CommonSerializers.entityTypeFormat

  private[this] lazy val anyEntityIdShape =
    (__ \ "id").format[String] and
    (__ \ "entityType").format[EntityType]


  override implicit lazy val anyEntityIdFormat: Format[AnyEntityId] = {
    anyEntityIdShape(AnyEntityId.apply, anyId => (anyId.value, anyId.entityType))
  }

  override implicit lazy val entityIdWriter: Writes[EntityId] = Writes {
    id => anyEntityIdFormat.writes(AnyEntityId(id.value, id.entityType))
  }

  implicit class ReadsTypedEntityId(reads: Reads.type) extends ReadsIdOps {

    override def id[T <: EntityId : ClassTag](from: String => T): Reads[T] = Reads {
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

  implicit class FormatsTypedEntityId(formats: Format.type) extends FormatsIdOps
}

object TypedEntityIdFormat extends EntityIdSerializers with TypedEntityIdFormat


trait MongoEntityIdFormat extends EntityIdFormat with WritesEntityId {
  self: EntityIdSerializers =>

  override implicit lazy val entityIdWriter: Writes[EntityId] = Writes[EntityId] {
    it =>
      Json.obj("_id" -> Json.obj("$oid" -> it.value))
  }

  implicit class ReadsMongoId(reads: Reads.type) extends ReadsIdOps {

    override def id[T <: EntityId : ClassTag](from: String => T): Reads[T] = Reads {
      json =>
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

  protected implicit class FormatsMongoId(format: Format.type) extends FormatsIdOps

}

object MongoEntityIdFormat extends EntityIdSerializers with MongoEntityIdFormat

trait MongoTypedEntityIdFormat extends EntityIdFormat with WritesEntityId with FormatsAnyEntityId {
  self: EntityIdSerializers =>

  import CommonSerializers.entityTypeFormat
  import language.implicitConversions

  private[this] lazy val entityIdShape: OFormat[(String, EntityType)] =
    (__ \ "_id").format(
      (__ \ "$oid").format[String] and
      (__ \ "entityType").format[EntityType] tupled
    )

  override lazy val entityIdWriter: Writes[EntityId] = Writes {
    id => entityIdShape.writes(id.value, id.entityType)
  }

  override lazy val anyEntityIdFormat: Format[AnyEntityId] = Format(
    Reads {
      it => entityIdShape.reads(it) map {
        case (value, entityType) => AnyEntityId(value, entityType)
      }
    },
    entityIdWriter
  )

  implicit class ReadsTypedMongoId(reads: Reads.type) extends ReadsIdOps {

    override def id[T <: EntityId : ClassTag](from: String => T): Reads[T] = {
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

  implicit class FormatsTypedMongoId(formats: Format.type) extends FormatsIdOps()(implicitly, entityIdWriter)
}

object MongoTypedEntityIdFormat extends EntityIdSerializers with EntityIdFormat with MongoTypedEntityIdFormat