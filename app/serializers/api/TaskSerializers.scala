package serializers.api

import models.tasks.{TaskId, Task}
import play.api.libs.json.{Reads, Json}
import serializers.{StringEntityIdFormat, EntityIdSerializers, Serializers}

trait TaskIdSerializers extends EntityIdSerializers with StringEntityIdFormat {

  implicit val taskIdJsonReader = Reads id TaskId
}

object TaskSerializers extends Serializers with TaskIdSerializers {

  implicit lazy val taskFormat = Json.format[Task]
}
