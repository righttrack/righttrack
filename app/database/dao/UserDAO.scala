package database.dao

import models.users.User
import models.common.Email
import database.{CreateResult, FindResult}

trait UserDAO {

  def get(id: String): FindResult[User]

  def get(email: Email): FindResult[User]

  def create(user: User): CreateResult[User]

}
