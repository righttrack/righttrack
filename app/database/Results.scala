package database

import models.Entity

sealed trait Result {
  this: Product =>
}

sealed trait FindResult[+E <: Entity] {
  def toOption: Option[Seq[E]] = FindResult.toOption(this)
}

object FindResult {

  def toOption[E <: Entity](result: FindResult[E]): Option[Seq[E]] = result match {
    case NotFound => None
    case found: Found[E] => Some(found.entities)
  }
}

case object NotFound extends FindResult[Nothing]

class Found[E <: Entity](val entities: Seq[E]) extends FindResult[E]

object Found {

  def apply[E <: Entity](entities: TraversableOnce[E]): FindResult[E] = new Found(entities.toSeq)

  def unapply[E <: Entity](result: FindResult[E]): Option[Seq[E]] = FindResult.toOption(result)
}


sealed trait CreateResult[+E <: Entity] {
  def created: Boolean
}

// TODO: Make common database exception groups?

case class NotCreated(error: Option[Exception])
  extends CreateResult[Nothing] {

  def created: Boolean = false
}

object NotCreated {
  def apply(exception: Exception): NotCreated = new NotCreated(Some(exception))
  def apply(): NotCreated = new NotCreated(None)
}

case class Created[+T <: Entity](entity: T)
  extends CreateResult[T] {

  def created: Boolean = true
}
