package models.users

import models.common.CommonSerializers
import play.api.libs.json.Json

object Serializers extends CommonSerializers {
  implicit lazy val userJsonWriter = Json.writes[User]
  implicit lazy val userJsonReader = Json.reads[User]
}
