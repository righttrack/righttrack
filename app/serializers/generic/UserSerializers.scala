package serializers.generic

import models.users.{User, Name, UserId}
import play.api.libs.json.{Json, Format}
import serializers._

trait UserIdSerializers extends Serializers {
  self: SerializerFormat =>

  implicit lazy val userIdFormat: Format[UserId] = Format id UserId
}

trait UserSerializers extends UserIdSerializers
with CommonSerializers
with AuthSerializers {
  self: SerializerFormat =>

  implicit lazy val nameFormat: Format[Name] = Json.format[Name]

  implicit lazy val userFormat: Format[User] = Json.format[User]
}