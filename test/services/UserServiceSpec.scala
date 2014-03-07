package services

import database.slick.h2.connection.H2InMemoryDBProvider
import database.slick.h2.dao.H2UserDAO
import models.common.Email
import models.users.{UserId, User}
import org.specs2.mutable.Specification
import services.impl.JavaUUIDGenerator

class UserServiceSpec extends Specification {

  // TODO: Create a test injector
  val idGen = new JavaUUIDGenerator
  val dbProvider = new H2InMemoryDBProvider(getClass)
  val Users = new H2UserDAO(dbProvider)

  val kyle = User(UserId(idGen.next()), Email("testy@mailinator.com"), "Kyle Testy")
  val sam = User(UserId(idGen.next()), Email("sam@mailinator.com"), "Sam")

  "UserService" should {

    "create a user" in {
      val user = Users.create(kyle)
      user.isEmpty should beTypedEqualTo(true)
    }

    "retrieve a user by email" in {
      Users.create(sam)
      val user = Users.get(email = sam.email).toOption
      user should beTypedEqualTo(Some(Seq(sam)))
    }
  }
}
