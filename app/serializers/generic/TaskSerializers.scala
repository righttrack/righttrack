package serializers.generic

import models.tasks.{TaskId, Task}
import play.api.libs.json.{Format, Json}
import serializers.{EntityIdFormat, Serializers}

trait TaskIdSerializers extends Serializers {
  self: EntityIdFormat =>

  implicit lazy val taskIdJsonFormat = Format id TaskId
}

trait TaskSerializers extends TaskIdSerializers {
  self: EntityIdFormat =>

  implicit lazy val taskFormat = Json.format[Task]
}
