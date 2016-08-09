package util

import org.specs2.mutable.Specification

class ClassNameHelpersSpec extends Specification {

  "ClassNameHelpers" should {

    "get the class name of a outer class" in {
      val name = ClassNameHelpers.typeName(classOf[OuterClass])
      name mustEqual "util.OuterClass"
    }
  }
}

class OuterClass
