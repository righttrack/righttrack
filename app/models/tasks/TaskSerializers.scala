package models.tasks

import models.common.CommonEntitySerializers
import play.api.libs.json.{Reads, Json}

object TaskSerializers extends CommonEntitySerializers {

  implicit lazy val taskJsonWriter = Json.writes[Task]
  implicit lazy val taskIdJsonReader = Reads id TaskId
  implicit lazy val taskJsonReader = Json.reads[Task]
}
