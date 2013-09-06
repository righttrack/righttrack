package models.users

import models.Model
import models.common.Email

trait UserModel extends Model {
  def emailAddress: Email
  def name: String
}

case class User(email: String, name: String) extends UserModel {
  lazy val emailAddress = Email(email)
}

