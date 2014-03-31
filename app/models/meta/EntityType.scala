package models.meta

import models.Entity
import scala.reflect.{classTag, ClassTag}
import scala.util.{Failure, Success, Try}

/**
 * An EntityType instance can be used for fast matching and type information lookup.
 *
 * The full class name can be used to get the runtime class for reflection.
 * Fortunately, we don't always need to do these reflective things, because we can just
 * use this object itself for lookup, serialization, and pretty much any other task we
 * want, AND we can also match on it as if it were a constant.
 *
 * @note all [[models.EntityId]]s must define an EntityType.
 * @note the constructor is private so that this class can remain simple and leave
 *       all the pre-construction logic to the companion object.
 *
 * @constructor Takes a class tag and extracts the full name of the runtime class.
 * @param tag the ClassTag of the Entity
 */
final class EntityType private[EntityType] (val tag: ClassTag[_ <: Entity]) {

  /**
   * The full class name of the Entity, interned to allow faster reference equality checking
   * when comparing with a runtime class name.
   *
   * This is enough information to call the Java class loader to get the runtime Class[_]
   * information at runtime, which can then be used to get a Scala reflection
   * "instance mirror", when can then be used to do various self-reflective things.
   * Also, the class tag could be made public to allow access the class at runtime.
   */
  val className: String = tag.runtimeClass.getName.intern()
}

/**
 * EntityType acts as a cache of EntityType objects that can only be added to.
 * Since this can be mutated at runtime, we must have some convention we have to enforce:
 *
 * 1. Every instance must be created eagerly before any of them are accessed.
 *    For this, we should never make these as defs or lazy vals.
 *
 * 2. We initialize all instances with a single method that adds them to a map.
 *    For this, the apply method is the only way to construct instances of the
 *    EntityType class, so we don't have to worry about this, as the compiler
 *    will take care of this for us.
 *
 * This is helpful when serializing and deserializing EntityIds as it allows you to
 * match on known [[models.meta.EntityTypes]] and instantiate the correct type instance
 * at runtime.
 *
 * @see [[models.meta.EntityTypes]] for all id type instances.
 */
object EntityType {

  /**
   * Builds a new instance of an EntityType and adds it to a singleton map.
   *
   * @note These should ALWAYS be instantiated with `val` and not `def`, `var`, or `lazy val`
   *
   *@throws IllegalArgumentException if the type of Entity has already been added.
   */
  final private[meta] def apply[T <: Entity : ClassTag]: EntityType = {
    val tag = classTag[T]
    val newType = new EntityType(tag)
    if (EntityType.all contains newType) {
      throw new IllegalArgumentException(s"EntityType for ${newType.className} is already defined inside the 'meta' package")
    }
    EntityType add newType
    newType
  }

  final private var _all: Set[EntityType] = Set.empty

  final private var _lookup: Map[String, EntityType] = Map.empty

  final private def add(mapping: EntityType) = {
    _all += mapping
    _lookup += mapping.className -> mapping
  }

  final def all: Set[EntityType] = _all

  final def find(fullClassName: String): Option[EntityType] = _lookup get fullClassName

  final def tryFind(fullClassName: String): Try[EntityType] = find(fullClassName) match {
    case Some(entity) => Success(entity)
    case None => Failure(new NoSuchElementException(
      s"No EntityType named $fullClassName. This must match the full class name of the Entity."
    ))
  }

  final def find(byClass: Class[_]): Option[EntityType] = find(byClass.getName)

  final def unapply(fullClassName: String): Option[EntityType] = find(fullClassName)

  final def unapply(byClass: Class[_]): Option[EntityType] = find(byClass.getName)

}