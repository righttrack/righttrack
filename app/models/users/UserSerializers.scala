package models.users

import models.common.CommonEntitySerializers
import play.api.libs.json.{Reads, Json}

object UserSerializers extends CommonEntitySerializers {

  implicit lazy val userJsonWriter = Json.writes[User]
  implicit lazy val userIdJsonReader = Reads id UserId
  implicit lazy val userJsonReader = Json.reads[User]
}
