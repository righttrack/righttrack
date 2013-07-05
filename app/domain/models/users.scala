package models

import play.api.libs.json.Json

package object user {

  object Serializers {
    implicit lazy val userJsonWriter = Json.writes[User]
    implicit lazy val userJsonReader = Json.reads[User]
  }

  trait UserModel {
    def id: String
    def name: String
  }

  case class User(id: String, name: String) extends UserModel

  val AnonUser = User("__anonuser__", "__anonuser__")

}
