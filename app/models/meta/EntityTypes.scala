package models.meta

import models.activity.Activity
import models.common.Event
import models.github.events.Repository
import models.tasks.{TaskList, Task}
import models.users.User

/**
 * The enumeration of all entity types.
 *
 * These act as a two-way binding from runtime object to serializable type information
 * @see [[models.meta.EntityType]]
 *
 * This is where we will define all our entity types for now. You should make nested objects
 * instead of using a common prefix, so that you can always import the shorter named versions
 * when you are only working within a specialized scope.
 *
 * If the file gets unwieldy, we can always create object singletons outside of this file in
 * the `meta` package and reference them here as `val`s.
 *
 * @note We should NEVER define any of these with `lazy val`, `def`, or `var`
 * @note We should always be able to access all EntityTypes from this one singleton object.
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
