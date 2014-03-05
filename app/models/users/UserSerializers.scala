package models.users

import models.common.CommonSerializers
import play.api.libs.json.Json
import models.ReadsId

object UserSerializers extends CommonSerializers {

  implicit lazy val userJsonWriter = Json.writes[User]
  implicit lazy val userIdJsonReader = ReadsId[UserId]
  implicit lazy val userJsonReader = Json.reads[User]
}
