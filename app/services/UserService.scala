package services

import models.results.{DatabaseException, CreateResult, RetrieveResult}
import models.users.User
import models.common.Email
import database.dao.UserDAO
import javax.inject.{Singleton, Inject}

@Singleton
class UserService @Inject() (dao: UserDAO) {

  def get(id: String): RetrieveResult[User] = ???

  def get(email: Email): RetrieveResult[User] = ???

  def create(user: User): CreateResult[DatabaseException, User] = {
    dao.create(user)
  }

}
