package models.tasks

import models.{EntityId, TypedEntityModel}

case class TaskId(value: String) extends EntityId
case class Task(id: TaskId, description: String, completed: Boolean = false) extends TypedEntityModel

case class TaskListId(value: String) extends EntityId