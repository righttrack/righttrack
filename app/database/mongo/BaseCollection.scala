package database.mongo

import scala.concurrent.ExecutionContext
import reactivemongo.core.commands.LastError
import models.Entity
import database.{Created, CreatedError, CreateResult}
import scala.reflect.ClassTag

trait BaseCollection {

  implicit protected def executionContext: ExecutionContext = ExecutionContext.global

  def toCreateResult[E <: Entity : ClassTag](of: E)(error: LastError): CreateResult[E] =
    if (error.ok) Created(of)
    else CreatedError[E](error.message)
}

