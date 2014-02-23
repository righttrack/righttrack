package models.tasks

import models.{EntityId, Entity}

case class TaskId(value: String) extends EntityId
case class Task(id: TaskId, description: String, completed: Boolean = false) extends Entity

case class TaskListId(value: String) extends EntityId