package database.mongo

import database.DatabaseError
import database.dao.{NotCreated, Created, CreateResult}
import models.Entity
import reactivemongo.core.commands.LastError

private[mongo]
sealed trait MongoCreateResult[+T <: Entity] extends CreateResult[T]

private[mongo] object MongoCreateResult {

  def apply[T <: Entity](entity: T)(last: LastError): MongoCreateResult[T] = {
    if (last.inError) MongoError(last)
    else MongoCreated(entity)
  }
}

private[mongo] case class MongoCreated[T <: Entity](value: T) extends Created[T] with MongoCreateResult[T] {

  override def success: Boolean = true

  override def it: Option[T] = Some(value)
}

private[mongo] case class MongoError(last: LastError) extends NotCreated with MongoCreateResult[Nothing] {

  override def success: Boolean = false

  override def it: Option[Nothing] = None

  override def error: Throwable with DatabaseError = last.asInstanceOf[LastError with DatabaseError]
}
