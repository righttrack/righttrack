package serializers.mongo

import models.users.Name
import models.users.User
import models.users.UserId
import play.api.libs.json.{Json, Format}
import serializers._
import serializers.api.AuthSerializers
import models.auth.AuthAccount

trait MongoUserIdSerializers extends EntityIdSerializers with MongoEntityIdFormat {

  implicit lazy val userIdFormat: Format[UserId] = Format id UserId
}

object MongoUserSerializers extends UserSerializers with Serializers with MongoUserIdSerializers with CommonSerializers {

  // For User format
  private implicit def authAccountFormat: Format[AuthAccount] = AuthSerializers.authAccountFormat
  
  implicit lazy val nameFormat: Format[Name] = Json.format[Name]

  override implicit lazy val userFormat: Format[User] = Json.format[User]
}