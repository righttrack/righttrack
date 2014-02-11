package models.tasks

import play.api.libs.json.Json
import models.{ReadsId, WritesId}

object TaskSerializers {
  implicit lazy val taskIdJsonWriter = WritesId[TaskId]
  implicit lazy val taskIdJsonReader = ReadsId[TaskId]
  implicit lazy val taskJsonWriter = Json.writes[Task]
  implicit lazy val taskJsonReader = Json.reads[Task]
}
