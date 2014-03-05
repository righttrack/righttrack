package models.common

import org.joda.time.DateTime
import models.EntityId

trait Event[T] {

  val id: EventId

  val data: T

  val timestamp: DateTime
}


case class EventId(value: String) extends AnyVal with EntityId