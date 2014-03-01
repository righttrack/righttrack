package models.common

import models.EntityId
import models.meta.{EntityTypes, EntityType}
import org.joda.time.DateTime

trait Event[T] {

  val id: EventId

  val data: T

  val timestamp: DateTime
}


case class EventId(value: String) extends AnyVal with EntityId {

  def entityType: EntityType = EntityTypes.Event
}