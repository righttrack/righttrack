package serializers.mongo

import models.auth.AuthAccount
import models.users.User
import models.users.UserId
import play.api.libs.json.{Json, Format}
import serializers._
import serializers.api.AuthSerializers

trait MongoUserIdSerializers extends EntityIdSerializers with MongoEntityIdFormat {

  implicit lazy val userIdReads: Format[UserId] = Format id UserId
}

object MongoUserSerializers extends UserSerializers with Serializers with MongoUserIdSerializers with CommonSerializers {

  override implicit lazy val authAccountFormat: Format[AuthAccount] = AuthSerializers.authAccountFormat

  override implicit lazy val userFormat: Format[User] = Json.format[User]
}