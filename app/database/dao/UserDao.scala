package database.dao

import models.results.{DBException, CreateResult, RetrieveResult}
import models.users.User
import models.common.Email

trait UserDAO {

  def get(id: String): RetrieveResult[User]

  def get(email: Email): RetrieveResult[User]

  def create(user: User): CreateResult[DBException, User]

}
