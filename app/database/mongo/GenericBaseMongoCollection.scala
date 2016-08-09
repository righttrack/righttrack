package database.mongo

import _root_.util.json.{PlayJson, PlayJsonFormatException}
import _root_.util.typelevel.Tagging._
import models.meta.EntityType
import models.{AnyEntityId, EntityId, Entity}
import play.api.data.validation.ValidationError
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
import play.modules.reactivemongo.json.BSONFormats
import play.modules.reactivemongo.json.collection.{JSONCollection, JSONQueryBuilder}
import reactivemongo.api._
import reactivemongo.api.collections._
import reactivemongo.bson._
import reactivemongo.bson.buffer.ReadableBuffer

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds
import scala.reflect.{ClassTag, classTag}
import scala.util.{Failure, Success, Try}

trait GenericBaseMongoCollection[
    Structure,
    Reader[Model] <: GenericMongoReader[Structure, Model],
    Writer[Model] <: GenericMongoWriter[Model, Structure]
  ]
  extends GenericCollection[Structure, Reader, Writer]
  with CollectionMetaCommands {

  type MongoCollection <: Collection
  
  def createEntity[T <: Entity](entity: T)(implicit writer: Writer[T], ec: ExecutionContext): Future[MongoCreateResult[T]] = {
    val entityObj = genericQueryBuilder.StructureWriter(writer).write(entity)
    insert(entityObj) map MongoCreateResult[T](entity)
  }

  protected def collection: MongoCollection

  override def failoverStrategy: FailoverStrategy = collection.failoverStrategy

  override def db: DB = collection.db

  override def name: String = collection.name

}

object Bson {

  def obj(fields: (String, JsValueWrapper)*): BSONDocument = {
    val json = Json.obj(fields: _*)
    fromJsonObjTry(json).get
  }

  def arr(fields: JsValueWrapper*): BSONArray = {
    val json = Json.arr(fields: _*)
    val readAttempt = fromJsonTry(json) flatMap {
      case array: BSONArray => Success(array)
      case bsonValue =>
        // this shouldn't happen since this should be a JsArray should be a BSONArray
        val bsonValuePretty = BSONDocument.pretty(BSONDocument(List("bson" -> bsonValue)))
        Failure(new RuntimeException(s"Cannot create a BSONArray from value of 'bson' in $bsonValuePretty"))
    }
    readAttempt.get
  }

  def documentReader[T: Reads: ClassTag]: BSONDocumentReader[T] = new BSONDocumentReader[T] {
    implicit lazy val reads: MongoReads[T] = MongoReads.fromJson[T]
    override def read(bson: BSONDocument): T = reads.readOrThrow(bson)
  }

  def documentWriter[T: OWrites: ClassTag]: BSONDocumentWriter[T] = new BSONDocumentWriter[T] {
    implicit lazy val writes: MongoOWrites[T] = MongoWrites.fromObjJson[T]
    override def write(model: T): BSONDocument = toBsonDocument(model)
  }

  def format[T: Format: ClassTag]: MongoFormat[T] = MongoFormat.fromJson[T]

  def formatId[T <: EntityId: ClassTag](fromString: String => T): MongoFormat[T] = new MongoFormat[T] {
    override def readOrThrow(bson: BSONValue): T = bson match {
      case oid: BSONObjectID => fromString(oid.stringify)
      case str: BSONString => fromString(str.value)
      case badType => throw new BsonFormatException(classTag[T].runtimeClass, badType)
    }
    override def writeOrThrow(id: T): BSONValue = {
      BSONObjectID(id.value)
    }
  }

  def formatTypedId[T <: EntityId](fromString: String => T)(tag: ClassTag[T]): MongoOFormat[T] = new MongoOFormat[T] {
    override def readOrThrow(bson: BSONValue): T = {
      val typedId = AnyEntityIdFormat.readOrThrow(bson)
      if (typedId.entityType.tag != tag) {
        throw new BsonFormatException(tag.runtimeClass, bson)
      }
      fromString(typedId.value)
    }
    override def writeOrThrow(id: T): BSONDocument = AnyEntityIdFormat.writeOrThrow(id)
  }

  implicit object AnyEntityIdFormat extends MongoOFormat[AnyEntityId] {
    override def readOrThrow(bson: BSONValue): AnyEntityId = bson match {
      case doc: BSONDocument =>
        val idValue = doc.getTry("id") map {
          id => TolerantObjectId.readOrThrow(id)
        }
        val idType = doc.getAsTry[String]("type") flatMap {
          className => EntityType.tryFind(className)
        }
        AnyEntityId(idValue.get, idType.get)
    }
    override def writeOrThrow(model: AnyEntityId): BSONDocument = {
      BSONDocument(Stream(
        "id" -> BSONObjectID(model.value),
        "type" -> BSONString(model.entityType.className)
      ))
    }
  }

  object TolerantObjectId extends MongoFormat[String] {
    override def readOrThrow(bson: BSONValue): String = bson match {
      case BSONString(value) => value
      case id: BSONObjectID => id.stringify
      case other => throw new BsonFormatException(classOf[String], other)
    }
    override def writeOrThrow(id: String): BSONValue = BSONString(id)
  }

  def formatEntity[T <: Entity](implicit format: Format[T], tag: ClassTag[T]): MongoOFormat[T] = {
    def runtimeClassName = tag.runtimeClass.getName
    // this should never happen as an Entity should always be an object with at least an id method and another field.
    require(
      format.isInstanceOf[OFormat[_]],
      s"Conversion to MongoOFormat[$runtimeClassName] requires an OFormat[$runtimeClassName], and not just a Format[_]"
    )
    MongoFormat.fromObjJson[T](format.asInstanceOf[OFormat[T]], tag)
  }

  def readsEntity[T <: Entity: Reads: ClassTag]: MongoReads[T] = MongoReads.fromJson[T]

  def writesEntity[T <: Entity](implicit writer: OWrites[T], tag: ClassTag[T]): MongoOWrites[T] = {
    def runtimeClassName = tag.runtimeClass.getName
    // this should never happen as an Entity should always be an object with at least an id method and another field.
    require(
      writer.isInstanceOf[OWrites[_]],
      s"Conversion to MongoOWrites[$runtimeClassName] requires an OWrites[$runtimeClassName], and not just a Writes[_]"
    )
    MongoWrites.fromObjJson[T]
  }

  def toBson[T](model: T)(implicit writer: MongoWrites[T]): BSONValue = writer.writeOrThrow(model)

  def toBsonDocument[T](model: T)(implicit writer: MongoOWrites[T]): BSONDocument = writer.writeOrThrow(model)

  def fromBson[T](bson: BSONValue)(implicit reader: MongoReads[T]): T = reader.readOrThrow(bson)

  @inline def toJson(bson: BSONValue): JsValue = BSONFormats.toJSON(bson)

  @inline def fromJson(json: JsValue): JsResult[BSONValue] = BSONFormats.toBSON(json)

  def fromJsonTry(json: JsValue): Try[BSONValue] = {
    fromJson(json) match {
      case JsSuccess(bson, _) =>
        Success(bson)
      case errors: JsError =>
        throw new PlayJsonFormatException(classOf[JsValue], json, errors)
    }
  }

  @inline def fromJsonOrThrow(json: JsValue): BSONValue = fromJsonTry(json).get

  def fromJsonObj(json: JsObject): JsResult[BSONDocument] = {
    fromJson(json) flatMap {
      case doc: BSONDocument => JsSuccess(doc)
      case bson => JsError("error.expected.jsobject")
    }
  }

  def fromJsonObjTry(json: JsObject): Try[BSONDocument] = {
    fromJsonTry(json) flatMap {
      case bson: BSONDocument => Success(bson)
      case bsonValue =>
        // this shouldn't happen since JsObject should always return a BSONDocument
        val bsonValuePretty = BSONDocument.pretty(BSONDocument(List("bson" -> bsonValue)))
        Failure(new RuntimeException(s"Cannot create a BSONDocument from the value of 'bson' in $bsonValuePretty"))
    }
  }

  @inline def fromJsonObjOrThrow[T: ClassTag](json: JsObject): BSONDocument = fromJsonObjTry(json).get

  def errorMessage(cls: Class[_], bson: BSONValue, reason: Option[String] = None): String = {
    val bsonString = bson match {
      case doc: BSONDocument => BSONDocument.pretty(doc)
      case value: BSONValue => value.toString
    }
    val reasonString = reason map { "Reason: " + _ } getOrElse ""
    s"Could not parse an instance of ${cls.getName} from the following BSON:\n$bsonString\n$reasonString"
  }
}

class BaseJsonCollection(override val collection: JSONCollection)
  extends GenericBaseMongoCollection[JsObject @@ SafeForMongo, MongoJsonReads, MongoJsonOWrites] {

  override type MongoCollection = JSONCollection

  override lazy val genericQueryBuilder: GenericQueryBuilder[JsObject @@ SafeForMongo, MongoJsonReads, MongoJsonOWrites] = {
    new BaseJsonQueryBuilder(collection.genericQueryBuilder.asInstanceOf[JSONQueryBuilder])
  }

  @inline override def StructureWriter[T](writer: MongoJsonOWrites[T]): GenericWriter[T, JsObject @@ SafeForMongo] =
    genericQueryBuilder.StructureWriter(writer)

  @inline override def StructureBufferWriter: BufferWriter[JsObject @@ SafeForMongo] =
    genericQueryBuilder.StructureBufferWriter

  @inline override def StructureReader[T](reader: MongoJsonReads[T]): GenericReader[JsObject @@ SafeForMongo, T] =
    genericQueryBuilder.StructureReader(reader)

  @inline override def StructureBufferReader: BufferReader[JsObject @@ SafeForMongo] =
    genericQueryBuilder.StructureBufferReader
}

case class BaseJsonQueryBuilder(builder: JSONQueryBuilder)
  extends GenericQueryBuilder[JsObject @@ SafeForMongo, MongoJsonReads, MongoJsonOWrites] {

  override type Self = BaseJsonQueryBuilder

  @inline override def failover: FailoverStrategy = builder.failover

  @inline override def projectionOption: Option[JsObject @@ SafeForMongo] = builder.projectionOption.map(tag[SafeForMongo](_))

  @inline override def merge: JsObject @@ SafeForMongo = tag[SafeForMongo](builder.merge)

  @inline override def hintOption: Option[JsObject @@ SafeForMongo] = builder.hintOption.map(tag[SafeForMongo](_))

  @inline override def options: QueryOpts = builder.options

  @inline override def commentString: Option[String] = builder.commentString

  @inline override def collection: Collection = builder.collection

  override def copy(
    queryOption: Option[JsObject @@ SafeForMongo],
    sortOption: Option[JsObject @@ SafeForMongo],
    projectionOption: Option[JsObject @@ SafeForMongo],
    hintOption: Option[JsObject @@ SafeForMongo],
    explainFlag: Boolean,
    snapshotFlag: Boolean,
    commentString: Option[String],
    options: QueryOpts,
    failover: FailoverStrategy
    ): Self = new BaseJsonQueryBuilder(builder)

  @inline override def sortOption: Option[JsObject @@ SafeForMongo] = builder.sortOption.map(tag[SafeForMongo](_))

  object structureReader extends MongoJsonReads[JsObject @@ SafeForMongo] {
    override implicit def reader: Reads[JsObject @@ SafeForMongo] = builder.structureReader.map(tag[SafeForMongo](_))
    override implicit def entityClass: ClassTag[JsObject @@ SafeForMongo] = classTag[JsObject @@ SafeForMongo]
  }

  @inline override def queryOption: Option[JsObject @@ SafeForMongo] = builder.queryOption.map(tag[SafeForMongo](_))

  @inline override def explainFlag: Boolean = builder.explainFlag

  @inline override def snapshotFlag: Boolean = builder.snapshotFlag

  @inline override def StructureWriter[T](writer: MongoJsonOWrites[T]): GenericWriter[T, JsObject @@ SafeForMongo] =
    new GenericWriter[T, JsObject @@ SafeForMongo] {
      override def write(t: T): JsObject @@ SafeForMongo = tag[SafeForMongo](writer.jsonOWrites.writes(t))
    }

  @inline override def StructureBufferWriter: BufferWriter[JsObject @@ SafeForMongo] = builder.StructureBufferWriter

  @inline override def StructureReader[T](reader: MongoJsonReads[T]): GenericReader[JsObject @@ SafeForMongo, T] =
    new GenericReader[JsObject @@ SafeForMongo, T] {
      override def read(json: JsObject @@ SafeForMongo): T = PlayJson.fromJsonOrThrow[T](json)(reader.reader, reader.entityClass)
    }

  @inline override def StructureBufferReader: BufferReader[JsObject @@ SafeForMongo] =
    new BufferReader[JsObject @@ SafeForMongo] {
      override def read(buffer: ReadableBuffer): JsObject @@ SafeForMongo =
        tag[SafeForMongo](builder.StructureBufferReader.read(buffer))
    }

}

class BsonFormatException(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause) {

  def this(cls: Class[_], bson: BSONValue) = this(Bson.errorMessage(cls, bson), null)

  def this(cls: Class[_], bson: BSONValue, reason: String) = this(Bson.errorMessage(cls, bson, Some(reason)), null)
}
