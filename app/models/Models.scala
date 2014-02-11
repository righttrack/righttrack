package models

/*
 * Message markers
 */

/**
 * A message between services or the front-end.
 */
private[models] trait Message {
  this: Product =>
}

/**
 * A case class view of some structured data.
 *
 * This can either be a view of some small portion of a domain model or the full set of model data.
 */
trait ModelView extends Message {
  this: Product =>
}

sealed trait EntityModel[IDType] extends ModelView {
  this: Product =>

  val id: IDType

  def is(that: EntityModel[IDType]): Boolean = this.id == that.id
}

/**
 * A full model of an entity. This should be a case class of everything up to but not including
 * the id of an entity.
 */
trait StringEntityModel extends EntityModel[String] {
  this: Product =>

  val id: String
}

/**
 * Testing
 */
trait EntityId extends Any {

  /**
   * The only value of an EntityId should be a string.
   */
  val value: String
}

trait TypedEntityModel extends EntityModel[EntityId] {
  this: Product =>

  val id: EntityId
}

/**
 * All write commands extend from Command.
 */
trait Command extends Message {
  this: Product =>
}

/**
 * All read queries extend from Command.
 */
trait Query extends Message {
  this: Product =>
}

