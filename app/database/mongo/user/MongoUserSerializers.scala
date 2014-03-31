package database.mongo.user

import database.mongo.MongoTypedEntityIdSerializers
import models.Serializers
import models.common.CommonSerializers
import models.users.{UserId, User}
import play.api.libs.json.{Json, Reads, Format}
import models.auth.{AuthAccount, AuthSerializers}

trait MongoUserSerializers extends Serializers with MongoTypedEntityIdSerializers {

  implicit def authAccountFormat: Format[AuthAccount]

  implicit def userIdReads: Format[UserId]

  implicit def userFormat: Format[User]
}

object MongoUserSerializers extends MongoUserSerializers with CommonSerializers {

  override implicit def authAccountFormat: Format[AuthAccount] = AuthSerializers.authAccountFormat

  override implicit def userIdReads: Format[UserId] = Format id UserId

  override implicit def userFormat: Format[User] = Json.format[User]
}