package database

import org.specs2.mutable.Specification
import scala.util.Success

class ResultsSpec extends Specification {

  "FindResults" should {

    "capture the first error" in {
      val result = for {
        a <- FoundOne("a")
        b <- FoundError[String]("b")
        c <- FoundError[String]("c")
      } yield c
      result must beAnInstanceOf[FoundError[String]]
      val cause = result.asInstanceOf[FoundError[String]].exception.cause
      cause must beAnInstanceOf[NotFoundException[String]]
      cause.asInstanceOf[NotFoundException[String]].why must endWith("b")
    }
  }
}
