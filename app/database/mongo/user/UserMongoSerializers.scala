package database.mongo.user

import database.mongo.CommonMongoSerializers
import models.common.Email
import models.users.{UserId, User}
import play.api.libs.json.{Json, Reads, Format}

trait UserMongoSerializers extends CommonMongoSerializers {

  implicit def emailFormat: Format[Email]

  implicit def userIdReads: Reads[UserId]

  implicit def userFormat: Format[User]
}

object UserMongoSerializers extends UserMongoSerializers with CommonMongoSerializers {

  override implicit def userIdReads: Reads[UserId] = Reads id UserId

  override implicit def userFormat: Format[User] = Json.format[User]
}