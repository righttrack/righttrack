package models.tasks

import models.{EntityId, Entity}
import models.meta.{EntityTypes, EntityType}

case class TaskId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.Task
}
case class Task(id: TaskId, description: String, completed: Boolean = false) extends Entity

case class TaskListId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.TaskList
}

class TaskList