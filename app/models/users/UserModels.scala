package models.users

import models.{EntityId, Entity}
import models.common.Email

case class User(id: UserId, email: Email, name: String) extends Entity

case class UserId(value: String) extends AnyVal with EntityId
