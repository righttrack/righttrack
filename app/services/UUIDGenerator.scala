package services

import models.EntityId

trait UUIDGenerator {

  def next(): String
}

trait EntityIdGenerator {

  def next[T <: EntityId](makeEntityId: String => T): T
}
