package services

import org.specs2.mutable.Specification
import scala.slick.session.Database
import models.common.Email
import models.users.User
import org.specs2.execute.Pending

class TestUserService extends Specification {

  def users = new UserService(Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver"))

  val kyle = User("testy@mailinator.com", "Kyle Testy")

  "UserService" should {
    "create a user" in {
//      val user = users.create(kyle)
//      user.created
      Pending("Under active development")
    }

    "retrieve a user by email" in {
//      users.create(kyle)
//      val user = users.get(email = Email("testy@mailinator.com")).toOption
//      user should beSome (kyle)
      Pending("Under active development")
    }
  }

}
