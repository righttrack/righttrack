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

private[database]
trait NotCreated extends CreateResult[Nothing] {

  override def success: Boolean = false

  override def getOrThrow: Nothing = throw error

  def error: Throwable with DatabaseError
}
