package models

import org.specs2.mutable.Specification

class TestEntityModel extends Specification {

  case class TestEntity(id: String, name: String, value: Int) extends StringEntityModel

  "EntityModel" should {

    "be able to check identity" in {
      val One = TestEntity("1", "one", 1)
      val OneAsTwo = TestEntity("1", "two", 2)
      One is OneAsTwo
    }
  }

}
