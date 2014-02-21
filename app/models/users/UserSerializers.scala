package models.users

import models.common.CommonSerializers
import play.api.libs.json.Json
import models.{ReadsId, WritesId}

object UserSerializers extends CommonSerializers {
  implicit lazy val userIdJsonWriter = WritesId[UserId]
  implicit lazy val userJsonWriter = Json.writes[User]
  implicit lazy val userIdJsonReader = ReadsId[UserId]
  implicit lazy val userJsonReader = Json.reads[User]
}
