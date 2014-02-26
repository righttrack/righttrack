package database.mongo

import scala.concurrent.ExecutionContext
import reactivemongo.core.commands.LastError
import models.Entity
import database.{Created, NotCreated, CreateResult}

trait BaseCollection {

  implicit protected def executionContext: ExecutionContext = ExecutionContext.global

  def toCreateResult[E <: Entity](of: E)(error: LastError): CreateResult[E] = {
    if (error.ok) Created(of)
    else NotCreated(error)
  }
}

