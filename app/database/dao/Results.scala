package database.dao

import database.DatabaseError
import models.Entity

private[database]
trait CreateResult[+T <: Entity] {

  def success: Boolean

  def it: Option[T]

  def getOrThrow: T
}

private[database]
trait Created[+T <: Entity] extends CreateResult[T] {

  def success: Boolean = true

  override def getOrThrow: T = value

  def value: T
}

object Created {

  def unapply[T <: Entity](result: CreateResult[T]): Option[T] = result match {
    case success: Created[T] => Some(success.value)
    case _ => None
  }
}

private[database]
trait NotCreated extends CreateResult[Nothing] {

  override def success: Boolean = false

  override def getOrThrow: Nothing = throw error

  def error: Throwable with DatabaseError
}

object NotCreated {

  def unapply(result: CreateResult[Entity]): Option[Throwable with DatabaseError] = result match {
    case failed: NotCreated => Some(failed.error)
    case _ => None
  }
}
