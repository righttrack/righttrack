package serializers

import models.meta.EntityType
import models.{EntityId, AnyEntityId}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.reflect.{classTag, ClassTag}
import org.joda.time.{DateTimeZone, DateTime}

// TODO: Document
/**
 * The common base type of all Serializers.
 */
private[serializers] trait Serializers {
  self: SerializerFormat =>

  implicit protected def postfixOk = scala.language.postfixOps

  protected implicit def formatToFormatOps(format: Format.type) = FormatOps

  object TypedReads

  object TypedFormat

}

object ReadsOps {

  def enum[T <: Enumeration](enum: T): Reads[enum.Value] = Reads {
    json => {
      val name = json.as[String]
      enum.values.find(_ == name) match {
        case Some(value) => JsSuccess(value)
        case None => JsError(s"Unrecognized value for $enum enumeration.")
      }
    }
  }
}

object WritesOps {

  def enum[T <: Enumeration](enum: T): Writes[enum.Value] = Writes {
    it => JsString(it.toString)
  }
}

object FormatOps {

  def enum[T <: Enumeration](enum: T): Format[enum.Value] = Format(
    ReadsOps.enum(enum),
    WritesOps.enum(enum)
  )
}

trait ReadsIdOps {

  def id[T <: EntityId : ClassTag](from: String => T): Reads[T]
}

trait FormatIdOps {

  def id[T <: EntityId : ClassTag](from: String => T): Format[T]
}

//trait JsonFormatBuilder {
//
//  def reads[A]: Reads[A]
//
//  def writes[A]: Writes[A]
//
//  def format[A]: Format[A]
//}
//
//object PlayJsonFormatBuilder extends JsonFormatBuilder {
//
//  import language.experimental.macros
//
//  def reads[A]: Reads[A] = macro JsMacroImpl.readsImpl[A]
//
//  def writes[A]: Writes[A] = macro JsMacroImpl.writesImpl[A]
//
//  def format[A]: Format[A] = macro JsMacroImpl.formatImpl[A]
//}
//
//object MongoJsonFormatBuilder extends JsonFormatBuilder {
//
//  def reads[A]: Reads[A] = Json.reads[A]
//
//  def writes[A]: Writes[A] = Json.writes[A]
//
//  def format[A]: Format[A] = Json.format[A]
//}

// TODO: Document
sealed trait SerializerFormat {
  self: Serializers =>

  protected implicit def implicitConversionsOk = languageFeature.implicitConversions

//  /**
//   * Generates Json readers and writers.
//   */
//  protected def Json: JsonFormatBuilder

  implicit def datetimeZone: DateTimeZone

  def defaultDateTimeReader: Reads[DateTime] = Reads.DefaultJodaDateReads map (_ withZone datetimeZone)

  def defaultDateTimeWriter: Writes[DateTime] = Writes apply Writes.DefaultJodaDateWrites.writes

  /**
   * Writes an EntityId as a String.
   *
   * Readers must be defined individually.
   */
  protected def entityIdWriter: Writes[EntityId]

  /**
   * Reads / writes type information along with an EntityId
   */
  implicit def typedEntityIdFormat: OFormat[AnyEntityId]

  /**
   * Default for formatting [[org.joda.time.DateTime]]
   */
  final implicit def dateTimeFormat: Format[DateTime] = Format(defaultDateTimeReader, defaultDateTimeWriter)

  /**
   * Converts Reads to be able to read [[EntityId]]s
   */
  protected implicit def toReadsIdOps(reads: Reads.type): ReadsIdOps

  /**
   * Converts Format to be able to format [[EntityId]]s
   */
  protected implicit def toFormatIdOps(format: Format.type): FormatIdOps = new FormatIdOps {

    override def id[T <: EntityId : ClassTag](from: (String) => T): Format[T] = {
      Format(Reads id from, entityIdWriter)
    }
  }

  /**
   * Converts TypedReads to be able to read [[EntityId]]s
   */
  protected implicit def toReadsTypedIdOps(reads: TypedReads.type): ReadsIdOps

  /**
   * Converts TypedFormat to be able to read [[EntityId]]s
   */
  protected implicit def toFormatTypedIdOps(format: TypedFormat.type): FormatIdOps = new FormatIdOps {

    override def id[T <: EntityId : ClassTag](from: (String) => T): Format[T] =
      typedEntityIdFormat.inmap[T](id => from(id.value), id => AnyEntityId(id.value, id.entityType))
  }
}

/**
 * Provides implicit writer for EntityId subclasses.
 */
trait InternalSerializerFormat extends SerializerFormat {
  self: Serializers =>

  import CommonSerializers.entityTypeFormat

//  override protected def json: JsonFormatBuilder = PlayJsonFormatBuilder

  override implicit def datetimeZone: DateTimeZone = DateTimeZone.UTC

  override protected val entityIdWriter: Writes[EntityId] = Writes(it => JsString(it.value))

  class ReadsStringId extends ReadsIdOps {

    override def id[T <: EntityId : ClassTag](from: String => T): Reads[T] = Reads {
      case JsString(value) => JsSuccess(from(value))
      case _ => JsError(
        "Entity id must be a String. For extracting a typed entity id, use " +
        s"$TypedFormat to read an ${ classOf[AnyEntityId].getSimpleName }"
      )
    }
  }

  override protected implicit def toReadsIdOps(reads: Reads.type): ReadsIdOps = new ReadsStringId

  /**
   * Reads / writes type information along with an EntityId
   */
  override implicit lazy val typedEntityIdFormat: OFormat[AnyEntityId] = {
    val anyEntityIdShape =
      (__ \ "id").format[String] and
      (__ \ "entityType").format[EntityType]
    anyEntityIdShape(AnyEntityId.apply, anyId => (anyId.value, anyId.entityType))
  }

  /**
   * Provides formats for [[models.meta.EntityType]] and [[models.AnyEntityId]].
   */
  class ReadsTypedEntityId extends ReadsIdOps {

    override def id[T <: EntityId : ClassTag](from: String => T): Reads[T] = Reads {
      json =>
        val ExpectedClassName = classTag[T].runtimeClass.getName.intern()
        typedEntityIdFormat.reads(json).flatMap(
          anyId => {
            anyId.entityType.className match {
              case ExpectedClassName => JsSuccess(from(anyId.value))
              case unexpected => JsError(s"Unexpected entity id type: $unexpected")
            }
          }
        )
    }
  }

  override protected implicit def toReadsTypedIdOps(reads: TypedReads.type): ReadsIdOps = new ReadsTypedEntityId
}

trait MongoSerializerFormat extends SerializerFormat {
  self: Serializers =>

  import CommonSerializers.entityTypeFormat

//  override protected def json: JsonFormatBuilder = MongoJsonFormatBuilder

  override implicit def datetimeZone: DateTimeZone = DateTimeZone.UTC

  /**
   * Writes the EntityId as a String.
   */
  override protected val entityIdWriter: Writes[EntityId] = Writes(it => JsString(it.value))

  // TODO: Make this work with _id field
  class ReadsMongoId extends ReadsIdOps {

    override def id[T <: EntityId : ClassTag](from: String => T): Reads[T] = Reads {
      case JsString(value) => JsSuccess(from(value))
      case _ => JsError(
        "Entity id must be a String. For extracting a typed entity id, use " +
        s"$TypedFormat to read an ${ classOf[AnyEntityId].getSimpleName }"
      )
    }
  }

  override protected implicit def toReadsIdOps(reads: Reads.type): ReadsIdOps = new ReadsMongoId

  override lazy val typedEntityIdFormat: OFormat[AnyEntityId] = {
    val entityIdShape =
      (__ \ "_id").format(
        (__ \ "$oid").format[String] and
        (__ \ "entityType").format[EntityType] tupled
      )
    OFormat(
      id => entityIdShape.reads(id) map AnyEntityId.tupled,
      id => entityIdShape.writes(id.value, id.entityType)
    )
  }

  class ReadsTypedMongoId extends ReadsIdOps {

    override def id[T <: EntityId : ClassTag](from: String => T): Reads[T] = {
      val ExpectedClass = classTag[T].runtimeClass.getName.intern()
      Reads {
        json =>
          typedEntityIdFormat.reads(json) flatMap {
            case AnyEntityId(value, entityType) =>
              if (entityType.className eq ExpectedClass) JsSuccess(from(value))
              else JsError(s"Wrong type of entity. Expected $ExpectedClass, read $entityType")
          }
      }
    }
  }

  override protected implicit def toReadsTypedIdOps(reads: TypedReads.type): ReadsIdOps = new ReadsTypedMongoId
}