package serializers.generic

import models.tasks.{TaskId, Task}
import play.api.libs.json.{Format, Json}
import serializers.{SerializerFormat, Serializers}

trait TaskIdSerializers extends Serializers {
  self: SerializerFormat =>

  implicit lazy val taskIdJsonFormat = Format id TaskId
}

trait TaskSerializers extends TaskIdSerializers {
  self: SerializerFormat =>

  implicit lazy val taskFormat = Json.format[Task]
}
