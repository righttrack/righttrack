package database.slick.h2.table

import database.slick.h2.Driver._
import models.Entity

abstract class UUIDEntityTable[ModelType <: Entity](schemaName: Option[String], tableName: String)
  extends Table[ModelType](schemaName, tableName) {

  def this(tableName: String) = this(None, tableName)

  type Columns <: Product

  def id = column[String]("id", O.PrimaryKey)

  def toEntity(fields: Columns): ModelType

  def fromEntity(entity: ModelType): Option[Columns]

}