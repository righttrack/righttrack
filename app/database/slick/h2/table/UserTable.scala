package database.slick.h2.table

import models.users.{UserId, User}
import models.common.Email

object UserTable extends UUIDEntityTable[User]("Users") {

  // TODO: Check out using a custom field mapper for Email
  def email = column[String]("email")
  def name = column[String]("name")

  def  *       = (id ~ email ~ name) <> (toEntity _, fromEntity _)
  type Columns = (String, String, String)

  def toEntity(fields: Columns): User = fields match {
    case (id, email, name) => User(UserId(id), Email(email), name)
  }

  def fromEntity(entity: User): Option[Columns] = {
    Some(entity.id.value, entity.email.address, entity.name)
  }
}
