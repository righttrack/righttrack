package database.dao

import models.common.Email
import models.users.User
import scala.concurrent.Future

trait UserDAO {

  def get(id: String): Future[User]

  def get(email: Email): Future[User]

  def create(user: User): Future[User]

}
