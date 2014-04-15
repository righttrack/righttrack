package models.users

import models.common.Email
import models.meta.{EntityTypes, EntityType}
import models.{EntityId, Entity}
import models.auth.AuthAccount
import org.joda.time.DateTime

case class Name(first: String, last: String)

case class User(
  id: UserId,
  username: String,
  email: Email,
  name: Name,
  auth: Seq[AuthAccount] = Seq.empty,
  created: DateTime = DateTime.now()
) extends Entity

case class UserId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.User
}
