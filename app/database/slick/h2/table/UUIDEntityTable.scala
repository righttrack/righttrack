package database.slick.h2.table

import database.slick.h2.Driver._
import models.{UUIDEntity, FullModel}

abstract class UUIDEntityTable[ModelType <: FullModel](schemaName: Option[String], tableName: String)
  extends Table[UUIDEntity[ModelType]](schemaName, tableName) {

  object SimpleSyntax extends Implicits with SimpleQL

  def this(tableName: String) = this(None, tableName)

  type Columns <: Product

  def id = column[String]("id", O.PrimaryKey, O.AutoInc)

  def toEntity(fields: Columns): UUIDEntity[ModelType]

  def fromEntity(entity: UUIDEntity[ModelType]): Option[Columns]

}


//abstract class IntEntityTable[ModelType <: Entity[Int]](schemaName: Option[String], tableName: String)
//  extends Table[ModelType](schemaName, tableName) {
//
//  def this(tableName: String) = this(None, tableName)
//
//  type Columns <: Product
//
//  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
//
//  def toEntity(fields: Columns): Option[ModelType]
//
//  def fromEntity(entity: ModelType): Columns
//
//}