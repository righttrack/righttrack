package services

import org.specs2.mutable.Specification
import models.user.User
import domain.common.Email

class TestUserService extends Specification {

  def users = new UserService

  val kyle = User("testy@mailinator.com", "Kyle Testy")

  "UserService" should {
    "create a user" in {
      val user = users.create(kyle)
      user.created
    }

    "retrieve a user by email" in {
      users.create(kyle)
      val user = users.get(email = Email("testy@mailinator.com"))
      user should beEqualTo (kyle)
    }
  }

}
