package models.tasks

import play.api.libs.json.Json
import models.ReadsId
import models.common.CommonSerializers

object TaskSerializers extends CommonSerializers {
  implicit lazy val taskJsonWriter = Json.writes[Task]
  implicit lazy val taskIdJsonReader = ReadsId[TaskId]
  implicit lazy val taskJsonReader = Json.reads[Task]
}
