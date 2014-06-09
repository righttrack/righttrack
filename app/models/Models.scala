package models

import models.meta.EntityType
import org.joda.time.DateTime

/**
 * A universal trait for type-safe entity ids.
 *
 * This is useful for making these as instances of a value class by extending [[scala.AnyVal]].
 *
 * An EntityId guarantees that you will have all the information you need to identify an
 * [[models.Entity]], regardless of whether it was serialized or there are multiple
 * object instances of the same conceptual entity.
 *
 * It must define an [[models.meta.EntityType]] so that you can consistently serialize and
 * deserialize it along with the type of [[models.Entity]] it refers to.
 *
 * @note You should always use the [[models.EntityId]] extractor to match on the type of id
 *       when the compiler cannot assert that you have the right type of id.
 */
trait EntityId extends Any {

  /**
   * The only value of an [[models.EntityId]] should be a string.
   *
   * @note You should only compare values if you know the exact type of EntityId,
   *       otherwise you should use [[models.EntityId.is]].
   */
  def value: String

  /**
   * An entity type from the [[models.meta.EntityTypes]] object.
   */
  def entityType: EntityType

  def is(that: EntityId): Boolean =
    (this.entityType eq that.entityType) &&
    this.value == that.value
}

/**
 * Singleton object for extracting [[models.EntityId]]s.
 */
object EntityId {

  /**
   * An extractor for any kind of [[models.EntityId]]s.
   *
   * This will extract [[models.AnyEntityId]]s as well as other subclasses of [[models.EntityId]].
   *
   * @note You should always use this extractor when you have a generic [[models.EntityId]],
   *       even if you only care about a specific EntityId subclass type because you should also
   *       handle values of type [[models.AnyEntityId]]
   */
  def unapply(id: EntityId): Option[(String, EntityType)] = Some(id.value, id.entityType)
}

/**
 * A serializable entity id with no specific type.
 *
 * @note This should only be used for serialization. The provided case class extractor will
 *       only extract instances of this class.
 *
 * @param value the [[scala.Predef.String]] value of the id
 * @param entityType the type of entity as tagged by this id
 */
case class AnyEntityId(value: String, entityType: EntityType) extends EntityId

// TODO: Document
object AnyEntityId extends ((String, EntityType) => AnyEntityId) {

  import scala.language.implicitConversions

  implicit def copyFrom(entityId: EntityId): AnyEntityId = new AnyEntityId(entityId.value, entityId.entityType)
}


/**
 * An entity is anything you want to track changes to over time.
 *
 * Therefore, it must have an [[models.EntityId]]
 */
trait Entity {

  /**
   * id An identifier that is universally unique among a given [[models.meta.EntityType]]
   */
  val id: EntityId

  /**
   * Check for type-safe identity equality.
   *
   * A super-quick way to verify two entity objects refer to the same entity.
   *
   * @note This also works with values sent over the wire.
   */
  final def is(that: Entity): Boolean = this.id is that.id
}

// TODO: Document
trait TimeCreated {
  self: Entity =>

  val created: DateTime
}
