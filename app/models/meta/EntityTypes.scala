package models.meta

import models.activity.Activity
import models.common.Event
import models.github.events.Repository
import models.tasks.{TaskList, Task}
import models.users.User

/**
 * The enumeration of all entity types.
 *
 * These act as a two-way binding from runtime object to serializable type information.
 */
object EntityTypes {

  // These should all be in alphabetical order
  // Checkout the Lines Sorter plugin to do this with a hot key
  val Activity = EntityType[Activity]
  val Event = EntityType[Event[_]]
  val Repository = EntityType[Repository]
  val Task = EntityType[Task]
  val TaskList = EntityType[TaskList]
  val User = EntityType[User]
}
