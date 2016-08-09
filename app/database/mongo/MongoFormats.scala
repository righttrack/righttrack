package database.mongo

import _root_.util.json.PlayJson
import _root_.util.logging.DefaultLogger
import _root_.util.typelevel.Tagging._
import play.api.libs.json._
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, BSONDocument, BSONValue}

import scala.reflect.{classTag, ClassTag}

trait MongoFormat[T] extends MongoReads[T] with MongoWrites[T]
trait MongoOFormat[T] extends MongoReads[T] with MongoOWrites[T] with MongoFormat[T]

trait MongoFormatFromJson[T] extends MongoWritesFromJson[T] with MongoReadsFromJson[T] with MongoFormat[T]
trait MongoOFormatFromJson[T] extends MongoOWritesFromJson[T] with MongoReadsFromJson[T] with MongoOFormat[T]

object MongoFormat {

  def fromJson[T](implicit format: Format[T], tag: ClassTag[T]): MongoFormat[T] = new MongoFormatFromJson[T] {
    override implicit def clsTag: ClassTag[T] = tag
    override implicit def reader: Reads[T] = format
    override implicit def writer: Writes[T] = format
  }

  def fromObjJson[T](implicit format: OFormat[T], tag: ClassTag[T]): MongoOFormat[T] = new MongoOFormatFromJson[T] {
    override implicit def clsTag: ClassTag[T] = tag
    override implicit def reader: Reads[T] = format
    override implicit def writer: OWrites[T] = format
  }

  def of[T](implicit format: MongoFormat[T]): MongoFormat[T] = format
}

trait MongoOWrites[T] extends MongoWrites[T] {

  override def writeOrThrow(model: T): BSONDocument
}

trait MongoOWritesFromJson[T] extends MongoOWrites[T] {

  implicit def clsTag: ClassTag[T]

  def writer: OWrites[T]

  override def writeOrThrow(model: T): BSONDocument = Bson.fromJsonObjOrThrow(writer.writes(model))
}

trait MongoWrites[T] {

  def writeOrThrow(value: T): BSONValue
}

trait MongoWritesFromJson[T] extends MongoWrites[T] {

  implicit def clsTag: ClassTag[T]

  def writer: Writes[T]

  override def writeOrThrow(model: T): BSONValue = Bson.fromJsonOrThrow(writer.writes(model))
}

object MongoWrites {

  implicit def toDocumentWriter[T](implicit writes: MongoOWrites[T]): BSONDocumentWriter[T] = new BSONDocumentWriter[T] {
    override def write(t: T): BSONDocument = writes.writeOrThrow(t)
  }

  def fromJson[T](implicit writes: Writes[T], tag: ClassTag[T]): MongoWrites[T] = new MongoWritesFromJson[T] {
    override implicit def clsTag: ClassTag[T] = tag
    override def writer: Writes[T] = writes
  }

  def fromObjJson[T](implicit writes: OWrites[T], tag: ClassTag[T]): MongoOWrites[T] = new MongoOWritesFromJson[T] {
    override implicit def clsTag: ClassTag[T] = tag
    override def writer: OWrites[T] = writes
  }

  def of[T](implicit writes: Writes[T]): Writes[T] = writes
}

trait MongoReads[T] {

  def readOrThrow(bson: BSONValue): T
}

object MongoReads {

  implicit def toDocumentReader[T](implicit reads: MongoReads[T]): BSONDocumentReader[T] = new BSONDocumentReader[T] {
    override def read(bson: BSONDocument): T = reads.readOrThrow(bson)
  }

  def fromJson[T](implicit reads: Reads[T], tag: ClassTag[T]): MongoReads[T] = new MongoReadsFromJson[T] {
    override implicit def clsTag: ClassTag[T] = tag
    override def reader: Reads[T] = reads
  }

  def of[T](implicit reads: Reads[T]): Reads[T] = reads
}

trait MongoReadsFromJson[T] extends MongoReads[T] {

  implicit def clsTag: ClassTag[T]

  implicit def reader: Reads[T]

  override def readOrThrow(bson: BSONValue): T = PlayJson.fromJsonOrThrow(Bson.toJson(bson))
}

// Phantom types
// TODO: How to read from mongo?

trait SafeForMongo

trait ReadFromMongo

trait GenericMongoWriter[-Model, +Serial] {

  def writes(model: Model): Serial

  def writeSafe(model: Model): Serial @@ SafeForMongo = tag[SafeForMongo](writes(model))
}

object GenericMongoWriter {

  implicit object SafeToWriteJsObject extends MongoJsonOWrites[JsObject @@ SafeForMongo] {
    override implicit def entityClass: ClassTag[JsObject @@ SafeForMongo] = classTag[JsObject @@ SafeForMongo]
    override def writes(model: JsObject @@ SafeForMongo): JsObject @@ SafeForMongo = model
  }
}

trait GenericMongoReader[-Serial, +Model] {

  def readOrThrow(serial: Serial): Model
}

trait MongoJsonFormat[T] extends MongoJsonReads[T] with MongoJsonOWrites[T]

object MongoJsonFormat {

  def fromOFormat[T](implicit format: OFormat[T], clsTag: ClassTag[T]): MongoJsonFormat[T] =
    new MongoJsonFormat[T] {
      override def writes(model: T): JsObject = format.writes(model)
      override implicit def reader: Reads[T] = format
      override implicit def entityClass: ClassTag[T] = clsTag
    }
}

trait MongoJsonReads[T] extends GenericMongoReader[JsValue, T] {
  self =>

  implicit def entityClass: ClassTag[T]

  implicit def reader: Reads[T]

  def reads(serial: JsValue): JsResult[T] = PlayJson.fromJson[T](serial)

  override def readOrThrow(serial: JsValue): T = PlayJson.fromJsonOrThrow[T](serial)
}

object MongoJsonReads extends DefaultLogger {

  def apply[T](f: JsValue => JsResult[T])(implicit tag: ClassTag[T]): MongoJsonReads[T] = new MongoJsonReads[T] {
    override implicit def entityClass: ClassTag[T] = tag
    // TODO: Flatten out ObjectIds
    override implicit def reader: Reads[T] = Reads(f)
  }

  implicit def fromReads[T](implicit reader: Reads[T], tag: ClassTag[T]): MongoJsonReads[T] = apply(reader.reads)
}

trait MongoJsonWrites[T] extends GenericMongoWriter[T, JsValue] {

  implicit object jsonWrites extends Writes[T] {
    override def writes(o: T): JsValue = writeSafe(o)
  }
}

trait MongoJsonOWrites[T] extends GenericMongoWriter[T, JsObject] {

  implicit object jsonOWrites extends OWrites[T] {
    override def writes(o: T): JsObject = writeSafe(o)
  }

  implicit def entityClass: ClassTag[T]
}

object MongoJsonWrites {

  def apply[T](f: T => JsValue): MongoJsonWrites[T] = new MongoJsonWrites[T] {
    override def writes(value: T): JsValue = f(value)
  }

  implicit def fromWrites[T](implicit writer: Writes[T], tag: ClassTag[T]): MongoJsonWrites[T] = apply(writer.writes)
}

object MongoJsonOWrites {

  def apply[T](f: T => JsObject)(implicit clsTag: ClassTag[T]): MongoJsonWrites[T] = new MongoJsonWrites[T] {
    // TODO: Flatten out ObjectIds
    override def writes(value: T): JsObject = f(value)
  }

  implicit def fromOWrites[T](implicit writer: OWrites[T], tag: ClassTag[T]): MongoJsonWrites[T] = apply(writer.writes)
}