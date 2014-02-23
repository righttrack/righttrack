package models

/*
 * Message markers
 */

 // TODO: Is Message useful?

/**
 * A message between services or the front-end.
 */
private[models] trait Message extends Product

/**
 * A case class view of some structured data.
 *
 * This can either be a view of some small portion of a domain model or the full set of model data.
 */
trait ModelView extends Message

/**
 * A universal trait for type-safe entity ids.
 */
trait EntityId extends Any {

  /**
   * The only value of an EntityId should be a string.
   */
  def value: String
}

/**
 * An entity with a type-safe id.
 */
trait Entity extends ModelView {

  def id: EntityId

  def is(that: Entity): Boolean = this.id == that.id
}

/**
 * All write commands extend from Command.
 */
trait Command extends Message

/**
 * All read queries extend from Command.
 */
trait Query extends Message
