package serializers.api

import models.users.{Name, User, UserId}
import play.api.libs.json._
import serializers._

trait UserIdSerializers extends EntityIdSerializers with StringEntityIdFormat {

  implicit lazy val userIdFormat: Format[UserId] = Format id UserId
}

object UserSerializers extends UserSerializers with Serializers
  with CommonSerializers
  with AuthIdSerializers
  with UserIdSerializers
{

  // For User format
  private implicit def authAccountFormat = AuthSerializers.authAccountFormat

  implicit lazy val nameFormat: Format[Name] = Json.format[Name]

  override implicit lazy val userFormat: Format[User] = Json.format[User]
}
