package models

import org.specs2.mutable.Specification
import models.meta.{EntityTypes, EntityType}

case class TestEntity(id: TestEntityId, name: String, value: Int) extends Entity
case class TestEntityId(value: String) extends AnyVal with EntityId {

  override def entityType: EntityType = EntityTypes.User
}

class TestEntityModel extends Specification {

  "Entity" should {

    "only check identity" in {
      val one = TestEntity(TestEntityId("1"), "one", 1)
      val oneAsTwo = TestEntity(TestEntityId("1"), "two", 2)
      one is oneAsTwo
    }

    "check equality" in {
      val one = TestEntity(TestEntityId("1"), "one", 1)
      val oneAgain = TestEntity(TestEntityId("1"), "one", 1)
      one should be_=== (oneAgain)
    }
  }

}
