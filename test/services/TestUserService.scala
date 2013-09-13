package services

import org.specs2.mutable.Specification
import models.common.Email
import models.users.User
import org.specs2.execute.Pending
import database.slick.h2.dao.H2UserDAO
import services.impl.JavaUUIDGenerator
import database.slick.h2.connection.H2InMemoryDBProvider

class TestUserService extends Specification {

  // TODO: Create a test injector
  val idGen = new JavaUUIDGenerator
  val Users = new H2UserDAO(new H2InMemoryDBProvider("test_users"))

  val kyle = User(idGen.next(), Email("testy@mailinator.com"), "Kyle Testy")

  "UserService" should {
    "create a user" in {
      val user = Users.create(kyle)
      user.created should be_=== (true)
//      Pending("Under active development")
    }

    "retrieve a user by email" in {
//      users.create(kyle)
//      val user = users.get(email = Email("testy@mailinator.com")).toOption
//      user should beSome (kyle)
      Pending("Under active development")
    }
  }

}
