package database.slick.h2.dao

import javax.inject.{Inject, Singleton}
import models.results.{DBException, Created, CreateResult, RetrieveResult}
import models.users.User
import models.common.Email
import database.slick.h2.table.UserTable
import database.dao.UserDAO
import models.UUIDEntity
import services.UUIDGenerator
import database.slick.h2.connection.DatabaseProvider

@Singleton
class H2UserDAO @Inject() (dbProvider: DatabaseProvider, idGen: UUIDGenerator) extends H2DAO(dbProvider) with UserDAO {

  db withSession { implicit s: Session =>
    println("Creating UserTable with:")
    UserTable.ddl.createStatements foreach println
    UserTable.ddl.create
  }

  def get(id: String): RetrieveResult[User] = ???

  def get(email: Email): RetrieveResult[User] = ???

  def create(user: User): CreateResult[DBException, User] = {
    val entity = UUIDEntity(idGen.next, user)
    db withSession { implicit s: Session =>
      UserTable.insert(entity)
      val query = UserTable.where(_.id === entity.id)
      for {
        user <- query
      } yield user
    }
    val result = Created(entity)
    result
  }

}
