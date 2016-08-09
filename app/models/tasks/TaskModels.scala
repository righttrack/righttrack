package models.tasks

import models.{EntityId, Entity}
import models.meta.{EntityTypes, EntityType}
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}
import serializers.{IdSerializer, DefaultSerializerFormats}
import util.DateTimeHelpers

case class TaskId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.Task
}

object TaskId extends IdSerializer[TaskId]

case class Task(
  id: TaskId,
  description: String,
  completed: Boolean = false,
  created: DateTime = DateTimeHelpers.now) extends Entity

object Task extends DefaultSerializerFormats {

  implicit val format: Format[Task] = Json.format[Task]
}

case class TaskListId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.TaskList
}

object TaskListId extends IdSerializer[TaskListId]

case class TaskList(id: TaskListId, created: DateTime) extends Entity

object TaskList extends DefaultSerializerFormats {

  implicit val format: Format[TaskList] = Json.format[TaskList]
}