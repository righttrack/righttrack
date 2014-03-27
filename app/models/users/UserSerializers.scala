package models.users

import models.common.CommonEntitySerializers
import play.api.libs.json.{Reads, Json}

object UserSerializers extends CommonEntitySerializers {

  implicit lazy val userId = Reads id UserId
  implicit lazy val user = Json.format[User]
}
