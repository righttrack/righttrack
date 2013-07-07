package models

import play.api.libs.json.Json
import domain.common.Email

package object user {

  object Serializers {
    implicit lazy val userJsonWriter = Json.writes[User]
    implicit lazy val userJsonReader = Json.reads[User]
  }

  trait UserModel {
    def emailAddress: Email
    def name: String
  }

  case class User(email: String, name: String) extends UserModel {
    lazy val emailAddress = Email(email)
  }

  val AnonUser = User("__anonuser__", "__anonuser__")

}
