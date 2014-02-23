package models

import org.specs2.mutable.Specification

class TestEntityModel extends Specification {

  case class TestEntity(id: TestEntityId, name: String, value: Int) extends Entity
  case class TestEntityId(value: String) extends EntityId

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
