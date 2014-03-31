package models.users

import models.common.Email
import models.meta.{EntityTypes, EntityType}
import models.{EntityId, Entity}
import models.auth.AuthAccount

case class User(id: UserId, email: Email, name: String, auth: Seq[AuthAccount] = Seq.empty) extends Entity

case class UserId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.User
}
