package models

/*
 * Message markers
 */

/**
 * A message between services or the front-end.
 */
trait Message {
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

/**
 * A full model of an entity. This should be a case class of everything up to but not including
 * the id of an entity.
 */
trait FullModel extends ModelView {
  this: Product =>

//  final def id: Option[String] = throw new NoSuchElementException
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

/*
 * Entities
 */

/**
 * An aggregate of a model and an id.
 */
sealed trait Entity[IdType, +ModelType <: FullModel] {

  def id: IdType

  def model: ModelType

}

/**
 * An extensible entity that all entities should inherit from.
 *
 * All ids must be UUIDs.
 *
 * TODO: Enforce this with the type system?
 */
case class UUIDEntity[ModelType <: FullModel](id: String, model: ModelType)
  extends Entity[String, ModelType]


/**
 * Builds entities based on the ID type
 */
object Entity {

  def apply[ModelType <: FullModel](id: String, model: ModelType): Entity[String, ModelType] = UUIDEntity(id, model)

}

//trait Entity[PKType] {
//
//  /**
//   * The id of the entity or None, if this object is not persisted anywhere.
//   */
//  def id: Option[PKType]
//}

///**
// * An aggregate of a model and an id.
// */
//sealed trait Entity[PrimaryKeyType, ModelType <: FullModel] {
//
//  def pk: Option[PrimaryKeyType]
//
//  def model: ModelType
//
//}
//
///**
// * An extensible entity that all entities should inherit from.
// */
//abstract class BaseEntity[ModelType <: FullModel](val id: String, val model: ModelType)
//  extends Entity[String, ModelType] {
//
//  def pk = Some(id)
//}
//
///**
// * A simple case class entity.
// */
//case class CaseEntity[PrimaryKeyType, ModelType](id: PrimaryKeyType, model: ModelType)
//  extends Entity[PrimaryKeyType, ModelType] {
//
//  def pk = Some(id)
//}
//
//
//object Entity {
//
//  def apply[ModelType <: FullModel](id: String, model: ModelType): Entity[String, ModelType] =
//
//}

///**
// * An extensible entity that all entities should inherit from.
// */
//abstract class BaseEntity[ModelType <: FullModel](val id: String, val model: ModelType)
//  extends Entity[String, ModelType] {
//
//  def pk = Some(id)
//}

/*
 * Entity mixins
 */

/**
 * A mixin to add a simple view version of the entity.
 */
trait SimpleView[ViewType <: ModelView] {
  this: Entity[_, _] =>

  def simpleView: ViewType
}
