package models.results

import models.{Entity, FullModel, Message}

sealed trait Result extends Message {
  this: Product =>
}



trait RetrieveResult[+T <: FullModel] {
  def entity: Entity[String, T]

  implicit def toOption: Option[Entity[String, T]] = this match {
    case NotFound => None
    case found => Some(found.entity)
  }
}

case object NotFound extends RetrieveResult[Nothing] {
  def entity = throw new NoSuchElementException
}

case class RetrieveSome[T <: FullModel](entity: Entity[String, T]) extends RetrieveResult[T]



sealed trait CreateResult[+E <: DBException, T <: FullModel] {
  def created: Boolean
  def error: Option[E]
  def entity: Entity[String, T]  // TODO: Abstract the ID type at this layer ???
}

case class NotCreated[E <: DBException, T <: FullModel](exception: E)
  extends CreateResult[E, T] {

  def created: Boolean = false
  def error: Option[E] = Some(exception)
  def entity: Entity[String, T] = throw new NoSuchElementException
}

case class Created[T <: FullModel](created: Boolean, error: Option[DBException], entity: Entity[String, T])
  extends CreateResult[DBException, T]

object Created {

  def apply[T <: FullModel](entity: Entity[String, T]): Created[T] = new Created(true, None, entity)

}