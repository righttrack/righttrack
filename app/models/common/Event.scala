package models.common

import models.{TimeCreated, Entity, EntityId}
import models.meta.{EntityTypes, EntityType}
import org.joda.time.DateTime

trait Event[T] extends Entity with TimeCreated {

  override val id: EventId

  val data: T

  override val created: DateTime
}


case class EventId(value: String) extends AnyVal with EntityId {

  def entityType: EntityType = EntityTypes.Event
}