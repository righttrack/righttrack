package database.slick.h2.table

import models.users.User
import models.common.Email
import models.UUIDEntity


//trait EntityTable[ModelType <: FullModel, EntityType <: Entity[ModelType]] {
//  this: Table[EntityType] =>
//
//  type ValueColumns <: Product
//
//  type AllColumns <: Product
//
//  def id = column[String]("id", O.PrimaryKey, O.AutoInc)
//
//  def toEntity(fields: AllColumns): Option[Entity[ModelType]]
//
//  def fromEntity(entity: Entity[ModelType]): AllColumns
//
//}

//abstract class BaseEntityTable[ModelType <: FullModel](schemaName: Option[String], tableName: String)
//  extends Table[Entity[ModelType]](schemaName, tableName)
//  with EntityTable[ModelType, Entity[ModelType]] {
//
//  def this(tableName: String) = this(None, tableName)
//}
//
//abstract class CaseEntityTable[ModelType <: FullModel](schemaName: Option[String], tableName: String)
//  extends Table[CaseEntity[ModelType]](schemaName, tableName)
//  with EntityTable[ModelType, CaseEntity[ModelType]] {
//
//  def this(tableName: String) = this(None, tableName)
//}

object UserTable extends UUIDEntityTable[User]("Users") {
//  type ValueColumns = (String, String)
//  type AllColumns = (Option[Int], String, String)

  // TODO: Check out using a custom field mapper for Email
  def email = column[String]("email")
  def name = column[String]("name")

  def  *       = (id ~ email ~ name) <> (toEntity _, fromEntity _)
  type Columns = (String, String, String)

  def toEntity(fields: Columns): UUIDEntity[User] = fields match {
    case (id, email, name) => UUIDEntity(id, User(Email(email), name))
  }

  def fromEntity(entity: UUIDEntity[User]): Option[Columns] = {
    Some(entity.id, entity.model.email.address, entity.model.name)
  }


  //  def create = email ~ name <> (
//    (email: String, name: String) => User(Email(email), name),
//    (params: User) => User.unapply(params) map {
//      case (email, name) => (email.address, name)
//    }
//  )

//  def insert(user: Entity[User])(implicit session: Session) = {
//    val result = this.insert(user)
//    result
//  }

//  def create = email ~ name <> (
//    (email: String, name: String) => User(Email(email), name),
//    (params: User) => User.unapply(params) map {
//      case (email, name) => (email.address, name)
//    }
//  ) returning id



//  def toEntity(fields: Columns): User = fields match {
//    case (id, email, name) => User(id, Email(email), name)
//  }
//
//  def fromEntity(entity: User): Option[Columns] = {
//    User.unapply(entity) map {
//      case (id, email, name) => (id, email.address, name)
//    }
//  }
}
