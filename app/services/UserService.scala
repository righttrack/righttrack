package services

import javax.inject.{Inject, Singleton}
import models.common.Email
import models.users.User
import models.results.{CreateMessage, RetrieveMessage}
import scala.slick.session.Database
import database.h2.UserTable


@Singleton
class UserService(db: Database) {

  def get(id: String): RetrieveMessage[User] = ???

  def get(email: Email): RetrieveMessage[User] = ???

  def create(user: User): CreateMessage[Exception, User] = ???

}
