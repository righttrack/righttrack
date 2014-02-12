package database.slick.h2.dao

import javax.inject.{Inject, Singleton}
import database.slick.h2.table.UserTable
import database.dao.UserDAO
import database.slick.h2.connection.DatabaseProvider
import models.common.Email
import models.results._
import models.users.User

@Singleton
class H2UserDAO @Inject() (dbProvider: DatabaseProvider) extends H2DAO(dbProvider) with UserDAO {

  db withSession { implicit s: Session =>
    UserTable.ddl.create
  }

  def get(id: String): RetrieveResult[User] = {
    db withSession { implicit s: Session =>
      val query = Query(UserTable)
      query.filter(_.id === id).firstOption()
    }
  }

  def get(email: Email): RetrieveResult[User] = {
    db withSession { implicit s: Session =>
      val query = Query(UserTable).filter(_.email === email.address)
      query.firstOption()
    }
  }

  def create(user: User): CreateResult[DatabaseException, User] = {
    db withSession { implicit s: Session =>
      UserTable.insert(user)
    }
    Created(user)
  }

}
