package models.meta

import models.activity.ActivityId
import models.common.EventId
import models.github.events.RepositoryId
import models.tasks.{TaskListId, TaskId}
import models.users.UserId

/**
 * The enumeration of all entity types.
 *
 * These act as a two-way binding from runtime object to serializable type information.
 */
object EntityTypes {

  // These should all be in alphabetical order
  // Checkout the Lines Sorter plugin to do this with a hot key
  val Activity = EntityType[ActivityId]
  val Event = EntityType[EventId]
  val Repository = EntityType[RepositoryId]
  val Task = EntityType[TaskId]
  val TaskList = EntityType[TaskListId]
  val User = EntityType[UserId]
}
