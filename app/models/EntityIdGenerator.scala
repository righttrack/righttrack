package models


trait EntityIdGenerator {

  def next[T <: EntityId](makeEntityId: String => T): T
}

class JavaUUIDGenerator extends EntityIdGenerator {

  import java.util.UUID

  def next[T <: EntityId](makeEntityId: String => T): T = makeEntityId(UUID.randomUUID().toString)
}

class MongoObjectIdGenerator extends EntityIdGenerator {

  import reactivemongo.bson.BSONObjectID

  override def next[T <: EntityId](makeEntityId: (String) => T): T = makeEntityId(BSONObjectID.generate.toString())
}