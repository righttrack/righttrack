package models

import play.api.libs.json.Json

package object users {

  val AnonUser = User("__anonuser__", "__anonuser__")

  object Serializers {
    implicit lazy val userJsonWriter = Json.writes[User]
    implicit lazy val userJsonReader = Json.reads[User]
  }

}
