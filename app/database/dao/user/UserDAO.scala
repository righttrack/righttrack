package database.dao.user

import database.dao.BaseDAO
import models.common.Email
import models.users.{UserId, User}

trait UserDAO extends BaseDAO {

  def findById(id: UserId): FindsOne[User]

  def findByEmail(email: Email): FindsOne[User]

  def create(user: User): Creates[User]
}
