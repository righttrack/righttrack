package database.mongo

import database.dao.{BaseDAOTypes, BaseDAO}
import models.{EntityId, Entity}
import play.api.libs.json.{JsObject, Reads, Writes}
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.collections.GenericCollection
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait BaseCollection extends BaseDAO with BaseCollection.Implicits {

  override type Creates[T <: Entity] = Future[MongoCreateResult[T]]

  // TODO: Use HasExecutionContext
  implicit protected def executionContext: ExecutionContext = ExecutionContext.global

}

object BaseCollection {

  import scala.language.implicitConversions

  trait Implicits {
    implicits =>

    implicit def toWriter[T](implicit writes: MongoOWrites[T]): BSONDocumentWriter[T] =
      MongoWrites.toDocumentWriter(writes)

    implicit def toReader[T](implicit reads: MongoReads[T]): BSONDocumentReader[T] =
      MongoReads.toDocumentReader(reads)

    // TODO: Try making this not Generic or implement this with a type alias and implicitNotFound

    trait GenericCollectionOps[Structure, Reader[_], Writer[_]] {

      import BaseCollectionTypes._

      protected def collection: GenericCollection[Structure, Reader, Writer]

      implicit def toWriter[T](implicit writes: MongoOWrites[T]): Writer[T]

      implicit def toReader[T](implicit reads: MongoReads[T]): Reader[T]

      def structure(bson: BSONDocument): Structure

      implicit def structureWriter: Writer[Structure]

      def createEntity[E <: Entity](entity: E)(implicit writer: MongoOWrites[E], ec: ExecutionContext): Creates[E] = {
        val entityObj = collection.genericQueryBuilder.StructureWriter(toWriter(writer)).write(entity)
        collection.insert(entityObj) map { MongoCreateResult(entity)(_) }
      }

      def findById[Id <: EntityId, E <: Entity](id: Id)(implicit reader: MongoReads[E], idWriter: MongoWrites[Id], ec: ExecutionContext): FindsOne[E] = {
        collection.find(structure(BSONDocument(Stream("_id" -> idWriter.writeOrThrow(id))))).one[E]
      }

      def findOneEntity[E <: Entity](query: Structure)(implicit reader: MongoReads[E], writer: MongoOWrites[Structure], ec: ExecutionContext): FindsOne[E] = {
        val queryObj = collection.genericQueryBuilder.StructureWriter(toWriter(writer)).write(query)
        collection.find(queryObj).one[E]
      }
    }

    implicit class BSONCollectionOps(override val collection: BSONCollection)
      extends GenericCollectionOps[BSONDocument, BSONDocumentReader, BSONDocumentWriter] {

      @inline override def structure(bson: BSONDocument): BSONDocument = bson

      @inline override implicit def structureWriter: BSONDocumentWriter[BSONDocument] = BSONDocumentIdentity

      @inline override implicit def toWriter[T](implicit writes: MongoOWrites[T]): BSONDocumentWriter[T] = implicits.toWriter(writes)

      @inline override implicit def toReader[T](implicit reads: MongoReads[T]): BSONDocumentReader[T] = implicits.toReader(reads)
    }

  }

  object Implicits extends Implicits
}

trait BaseCollectionTypes extends BaseDAOTypes {

  override type Creates[T <: Entity] = Future[MongoCreateResult[T]]
}

object BaseCollectionTypes extends BaseCollectionTypes

