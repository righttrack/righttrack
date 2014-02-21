package services

import models.users.User
import models.common.Email
import database.dao.UserDAO
import javax.inject.{Singleton, Inject}
import database.{CreateResult, FindResult}

@Singleton
class UserService @Inject() (dao: UserDAO) {

  def get(id: String): FindResult[User] = ???

  def get(email: Email): FindResult[User] = ???

  def create(user: User): CreateResult[User] = {
    dao.create(user)
  }

}
