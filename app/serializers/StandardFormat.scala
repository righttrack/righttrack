package serializers

import _root_.util.NamedEnumeration
import models.{AnyEntityId, EntityId}
import play.api.libs.json._

import scala.reflect.ClassTag
import scala.util.{Failure, Success}

object StandardReads extends StandardReads with DefaultSerializerFormats

trait StandardReads extends ConstraintReads with PathReads {

  def asString[T](fromString: String => T): Reads[T] = new StringReads[T] {
    override def parse: (String) => T = fromString
  }

  def id[T <: EntityId](from: String => T): Reads[T] = Reads {
    case JsString(value) => JsSuccess(from(value))
    case _ => JsError(
      s"Entity id must be a String. For extracting an ${ classOf[AnyEntityId].getSimpleName }, use StandardReads.typedId"
    )
  }

  def typedId[Id <: EntityId](from: String => Id)(implicit reader: Reads[AnyEntityId], tag: ClassTag[Id]): Reads[Id] = {
    new Reads[Id] {
      val ExpectedClassName = tag.runtimeClass.getName.intern()
      override def reads(json: JsValue): JsResult[Id] = {
        reader.reads(json).flatMap(
          anyId =>
            anyId.entityType.className match {
              case ExpectedClassName => JsSuccess(from(anyId.value))
              case unexpected => JsError(s"error.id.expected: $ExpectedClassName")
            }
        )
      }
    }
  }

  def enum[T <: NamedEnumeration](enum: T): Reads[enum.Value] = Reads {
    json => {
      json.validate[String] flatMap {
        named =>
          enum.findValueTry(named) match {
            case Success(value) => JsSuccess(value)
            case Failure(ex) => JsError(s"error.enum.value.unknown: $named")
          }
      }
    }
  }
}

object StandardWrites extends StandardWrites with DefaultSerializerFormats

trait StandardWrites extends ConstraintWrites with PathWrites {

  def asString[T](stringify: T => String): Writes[T] = new StringWrites[T] {
    override def serialize: (T) => String = stringify
  }

  private object StringEntityIdWriter extends Writes[EntityId] {
    override def writes(id: EntityId): JsValue = JsString(id.value)
  }

  def id[T <: EntityId]: Writes[T] = StringEntityIdWriter.asInstanceOf[Writes[T]]

  def enum[T <: NamedEnumeration](enum: T): Writes[enum.Value] = new Writes[enum.Value] {
    override def writes(value: enum.Value): JsValue = JsString(value.name)
  }

  def typedId[Id <: EntityId](from: String => Id)(implicit writer: Writes[AnyEntityId]): Writes[Id] = new Writes[Id] {
    override def writes(id: Id): JsValue = {
      writer.writes(id.toAnyId)
    }
  }
}

object StandardFormat extends StandardFormat with DefaultSerializerFormats

trait StandardFormat extends ConstraintFormat with PathFormat {

  def asString[T](fromString: String => T, stringify: T => String): Format[T] = new StringFormat[T] {
    override def parse: (String) => T = fromString
    override def serialize: (T) => String = stringify
  }

  def id[Id <: EntityId](from: String => Id): Format[Id] = new Format[Id] {
    val reader = StandardReads.id(from)
    @inline def writer = StandardWrites.id[Id]  // just forward the reference along
    override def reads(json: JsValue): JsResult[Id] = reader.reads(json)
    override def writes(id: Id): JsValue = writer.writes(id)
  }

  def enum[T <: NamedEnumeration](enum: T): Format[enum.Value] = new Format[enum.Value] {
    private val reader: Reads[enum.Value] = StandardReads.enum(enum)
    private val writer: Writes[enum.Value] = StandardWrites.enum(enum)
    override def writes(enumValue: enum.Value): JsValue = writer.writes(enumValue)
    override def reads(json: JsValue): JsResult[enum.Value] = reader.reads(json)
  }
}

private[serializers] trait StringReads[T] extends Reads[T] {
  def parse: String => T
  override def reads(json: JsValue): JsResult[T] = Reads.StringReads.reads(json).map(parse)
}

private[serializers] trait StringWrites[-T] extends Writes[T] {
  def serialize: T => String
  override def writes(o: T): JsValue = JsString(serialize(o))
}

private[serializers] trait StringFormat[T] extends Format[T] with StringReads[T] with StringWrites[T]
