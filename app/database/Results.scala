package database

import models.{EntityId, Entity}

sealed trait Result {
  this: Product =>
}

sealed trait FindResult[+E] {
  def toOption: Option[Seq[E]] = FindResult.toOption(this)
}

object FindResult {

  def toOption[E](result: FindResult[E]): Option[Seq[E]] = result match {
    case NotFound => None
    case found: Found[E] => Some(found.entities)
  }
}

case object NotFound extends FindResult[Nothing]

class Found[E](val entities: Seq[E]) extends FindResult[E]

object Found {

  def apply[E](entities: TraversableOnce[E]): FindResult[E] = new Found(entities.toSeq)

  def unapply[E](result: FindResult[E]): Option[Seq[E]] = FindResult.toOption(result)
}


sealed trait CreateResult[+E <: Entity] {

  def created: Boolean

  def get: E

  def getId: EntityId = get.id

  def getOrElse[O >: E](otherwise: => O): O = {
    if (created) get
    else otherwise
  }
}

// TODO: Make common database exception groups?

class EntityNotCreated extends Exception

case class NotCreated(error: Option[Exception])
  extends CreateResult[Nothing] {

  override def created: Boolean = false

  override def get: Nothing = throw new EntityNotCreated
}

object NotCreated {
  def apply(exception: Exception): NotCreated = new NotCreated(Some(exception))
  def apply(): NotCreated = new NotCreated(None)
}

case class Created[+T <: Entity](entity: T)
  extends CreateResult[T] {

  override def created: Boolean = true

  override def get: T = entity
}
