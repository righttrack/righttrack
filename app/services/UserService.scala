package services

import javax.inject.Singleton
import models.user.User
import domain.common.Email
import results.CreateResult

@Singleton
class UserService {

  def get(id: String): User = ???

  def get(email: Email): User = ???

  def create(user: User): CreateResult[User] = ???

}
