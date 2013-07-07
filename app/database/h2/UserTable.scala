package database.h2

import scala.slick.driver.H2Driver.simple._
import models.users.User

object UserTable extends Table[User]("users") {
  def email = column[String]("email", O.PrimaryKey)
  def name = column[String]("name")
  def * = email ~ name <> (User.apply _, User.unapply _)
}
