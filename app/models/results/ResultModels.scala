package models.results

import models.{EntityModel, Message}

sealed trait Result extends Message {
  this: Product =>
}



trait RetrieveResult[+T <: EntityModel] {
  def entity: T

  def toOption = RetrieveResult.toOption(this)
}

case object NotFound extends RetrieveResult[Nothing] {
  def entity = throw new NoSuchElementException
}

case class RetrieveSome[T <: EntityModel](entity: T) extends RetrieveResult[T]

object RetrieveResult {

  implicit def fromOption[T <: EntityModel](found: Option[T]): RetrieveResult[T] = {
    found match {
      case Some(entity) => RetrieveSome(entity)
      case None => NotFound
    }
  }

  def toOption[T <: EntityModel](result: RetrieveResult[T]): Option[T] = result match {
    case NotFound => None
    case found => Some(found.entity)
  }
}



sealed trait CreateResult[+E <: DBException, T <: EntityModel] {
  def created: Boolean
  def error: Option[E]
  def entity: T  // TODO: Abstract the ID type at this layer ???
}

case class NotCreated[E <: DBException, T <: EntityModel](exception: E)
  extends CreateResult[E, T] {

  def created: Boolean = false
  def error: Option[E] = Some(exception)
  def entity: T = throw new NoSuchElementException
}

case class Created[T <: EntityModel](created: Boolean, error: Option[DBException], entity: T)
  extends CreateResult[DBException, T]

object Created {

  def apply[T <: EntityModel](entity: T): Created[T] = new Created(true, None, entity)

}