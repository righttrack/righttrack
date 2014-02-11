package models.results

import models.{EntityModel, Message}

sealed trait Result extends Message {
  this: Product =>
}



trait RetrieveResult[+T <: EntityModel[_]] {
  def entity: T

  def toOption = RetrieveResult.toOption(this)
}

case object NotFound extends RetrieveResult[Nothing] {
  def entity = throw new NoSuchElementException
}

case class RetrieveSome[T <: EntityModel[_]](entity: T) extends RetrieveResult[T]

object RetrieveResult {

  implicit def fromOption[T <: EntityModel[_]](found: Option[T]): RetrieveResult[T] = {
    found match {
      case Some(entity) => RetrieveSome(entity)
      case None => NotFound
    }
  }

  def toOption[T <: EntityModel[_]](result: RetrieveResult[T]): Option[T] = result match {
    case NotFound => None
    case found => Some(found.entity)
  }
}


// TODO: This stuff should be refactored to make more sense with Futures, which have their own notion of failure

sealed trait CreateResult[E <: DatabaseException, T <: EntityModel[_]] {
  def created: Boolean
  def error: Option[E]
  def entity: T  // TODO: Abstract the ID type at this layer ???
}

case class NotCreated[E <: DatabaseException, T <: EntityModel[_]](exception: E)
  extends CreateResult[E, T] {

  def created: Boolean = false
  def error: Option[E] = Some(exception)
  def entity: T = throw new NoSuchElementException
}

case class Created[T <: EntityModel[_]](created: Boolean, error: Option[DatabaseException], entity: T)
  extends CreateResult[DatabaseException, T]

object Created {

  def apply[T <: EntityModel[_]](entity: T): Created[T] = new Created(true, None, entity)

}