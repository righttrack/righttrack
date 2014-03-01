package models

import models.meta.EntityType

/**
 * A universal trait for type-safe entity ids.
 */
trait EntityId extends Any {

  /**
   * The only value of an EntityId should be a string.
   */
  def value: String

  /**
   * An entity type from the [[models.meta.EntityTypes]] object.
   */
  def entityType: EntityType
}

object EntityId {

  def unapply(id: EntityId): Option[(String, EntityType)] = Some(id.value, id.entityType)
}


/**
 * An entity with a type-safe id.
 */
trait Entity {

  def id: EntityId

  def is(that: Entity): Boolean = this.id == that.id
}
