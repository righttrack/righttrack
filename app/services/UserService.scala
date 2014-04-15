package services

import cake.GlobalExecutionContext
import database.dao.user.UserDAO
import javax.inject.{Singleton, Inject}
import models.common.Email
import models.users.{UserId, User}
import scala.concurrent.Future

// TODO: Convert to Actor
@Singleton
class UserService @Inject()(dao: UserDAO) extends GlobalExecutionContext {

  // TODO: Group these finds into a more logical query for the rest layer

  def findById(id: UserId): Future[Option[User]] =
    dao.findById(id)

  def findByEmail(email: Email): Future[Option[User]] =
    dao.findByEmail(email)

  def create(user: User): Future[User] =
    dao.create(user).map(_.getOrThrow)
}
