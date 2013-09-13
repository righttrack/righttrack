package database.slick.h2.dao

import javax.inject.{Inject, Singleton}
import models.results.{DBException, Created, CreateResult, RetrieveResult}
import models.users.User
import models.common.Email
import database.slick.h2.table.UserTable
import database.dao.UserDAO
import services.UUIDGenerator
import database.slick.h2.connection.DatabaseProvider

@Singleton
class H2UserDAO @Inject() (dbProvider: DatabaseProvider) extends H2DAO(dbProvider) with UserDAO {

  db withSession { implicit s: Session =>
    println("Creating UserTable with:")
    UserTable.ddl.createStatements foreach println
    UserTable.ddl.create
  }

  def get(id: String): RetrieveResult[User] = ???

  def get(email: Email): RetrieveResult[User] = ???

  def create(user: User): CreateResult[DBException, User] = {
    db withSession { implicit s: Session =>
      UserTable.insert(user)
      val query = UserTable.where(_.id === user.id)
      for {
        user <- query
      } yield user
    }
    val result = Created(user)
    result
  }

}
