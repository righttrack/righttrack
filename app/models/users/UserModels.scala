package models.users

import models.common.Email
import models.meta.{EntityTypes, EntityType}
import models.{EntityId, Entity}
import models.auth.AuthAccount
import org.joda.time.DateTime
import play.api.libs.json.{OFormat, Format, Json}
import serializers.IdSerializer
import util.json.PlayJson

case class Name(first: String, last: String)

object Name {

  implicit val format: Format[Name] = Json.format[Name]
}

case class User(
  id: UserId,
  username: String,
  email: Email,
  name: Name,
  auth: Seq[AuthAccount] = Seq.empty,
  created: DateTime = DateTime.now()
) extends Entity

object User {

  implicit val format: Format[User] = Json.format[User]
}

case class UserId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.User
}

object UserId extends IdSerializer[UserId]
