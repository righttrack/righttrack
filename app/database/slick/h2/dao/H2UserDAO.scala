package database.slick.h2.dao

import database.slick.h2.table.UserTable
import database.dao.UserDAO
import database.slick.h2.connection.DatabaseProvider
import models.common.Email
import models.users.User
import database.{Found, Created, CreateResult, FindResult}
import com.escalatesoft.subcut.inject.{Injectable, BindingModule}

class H2UserDAO(dbProvider: DatabaseProvider)
  (implicit val bindingModule: BindingModule)
  extends H2DAO(dbProvider)
  with UserDAO
  with Injectable {

  db withSession { implicit s: Session =>
    UserTable.ddl.create
  }

  def get(id: String): FindResult[User] = {
    db withSession { implicit s: Session =>
      val query = Query(UserTable)
      Found(query.filter(_.id === id).firstOption())
    }
  }

  def get(email: Email): FindResult[User] = {
    db withSession { implicit s: Session =>
      val query = Query(UserTable).filter(_.email === email.address)
      Found(query.firstOption())
    }
  }

  def create(user: User): CreateResult[User] = {
    db withSession { implicit s: Session =>
      UserTable.insert(user)
    }
    Created(user)
  }

}
