package services

import models.users.User
import models.common.Email
import database.dao.UserDAO
import database.{CreateResult, FindResult}
import com.escalatesoft.subcut.inject.{Injectable, BindingModule}

class UserService(dao: UserDAO)
  (implicit val bindingModule: BindingModule)
  extends Injectable {

  def get(id: String): FindResult[User] = ???

  def get(email: Email): FindResult[User] = ???

  def create(user: User): CreateResult[User] = {
    dao.create(user)
  }

}
