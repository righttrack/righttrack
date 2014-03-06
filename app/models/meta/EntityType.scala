package models.meta

import models.EntityId
import scala.reflect.{classTag, ClassTag}
import scala.util.{Failure, Success, Try}

/**
 * All EntityIds must define an IdType. You can think of this as all the information needed
 * to identify an entity.
 *
 * @param tag the ClassTag of the Entity
 */
class EntityType private[EntityType] (tag: ClassTag[_]) {

  val name: String = tag.runtimeClass.getName

  // an interned string for fast name equality checking
  val symbol: Symbol = Symbol(name)
}

/**
 * An enumeration of all EntityIdTypes.
 *
 * Helpful when serializing EntityIds. Allows you to match on known EntityIdTypes and use names to
 * instantiate instances of the correct type at runtime when deserializing.
 *
 * @see [[models.meta.EntityTypes]] for all id type instances.
 */
object EntityType {

  private[meta] def apply[T <: EntityId : ClassTag]: EntityType = {
    val tag = classTag[T]
    val newType = new EntityType(tag)
    if (EntityType.all contains newType) {
      throw new IllegalArgumentException(s"EntityType for ${newType.name} already exists")
    }
    EntityType add newType
    newType
  }

  private var _all: Set[EntityType] = Set.empty

  private var _lookup: Map[Symbol, EntityType] = Map.empty

  private def add(mapping: EntityType) = {
    _all += mapping
    _lookup += mapping.symbol -> mapping
  }

  def all: Set[EntityType] = _all

  def find(symbol: Symbol): Option[EntityType] = _lookup get symbol

  def find(name: String): Option[EntityType] = find(Symbol(name))

  def tryFind(symbol: Symbol): Try[EntityType] = find(symbol) match {
    case Some(entity) => Success(entity)
    case None => Failure(new NoSuchElementException(
      s"No EntityType with Id named $symbol. This must match the full class name of the EntityId."
    ))
  }

  def tryFind(name: String): Try[EntityType] = tryFind(Symbol(name))

  def find(byClass: Class[_]): Option[EntityType] = find(byClass.getName)

  def unapply(symbol: String): Option[EntityType] = find(symbol)

  def unapply(byClass: Class[_]): Option[EntityType] = find(byClass.getName)

}