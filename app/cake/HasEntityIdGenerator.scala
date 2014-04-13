package cake

import models.{JavaUUIDGenerator, MongoObjectIdGenerator, EntityIdGenerator}

trait HasEntityIdGenerator {

  def idGen: EntityIdGenerator
}

trait DefaultIdGen extends MongoIdGen

trait MongoIdGen extends HasEntityIdGenerator {

  def idGen: EntityIdGenerator = MongoObjectIdGenerator
}

trait UUIdGen extends HasEntityIdGenerator {

  def idGen: EntityIdGenerator = JavaUUIDGenerator
}
