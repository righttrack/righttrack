package models.tasks

import models.{EntityId, Entity}
import models.meta.{EntityTypes, EntityType}
import org.joda.time.DateTime

case class TaskId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.Task
}
case class Task(id: TaskId, description: String, completed: Boolean = false, created: DateTime = DateTime.now()) extends Entity

case class TaskListId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.TaskList
}

case class TaskList(id: TaskListId, created: DateTime) extends Entity