package models


trait EntityIdGenerator {

  def next(): String

  final def next[T <: EntityId](makeEntityId: String => T): T = makeEntityId(next())
}

object JavaUUIDGenerator extends EntityIdGenerator {

  import java.util.UUID

  override def next(): String = UUID.randomUUID().toString
}

object MongoObjectIdGenerator extends EntityIdGenerator {

  import reactivemongo.bson.BSONObjectID

  override def next(): String = BSONObjectID.generate.stringify
}
