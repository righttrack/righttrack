package models

import play.api.libs.json.Json
import models.common.{CommonSerializers, Email}

package object users {

  val AnonUser = User(Email("anonuser@mailinator.com"), "__anonuser__")

  object Serializers extends CommonSerializers {
    implicit lazy val userJsonWriter = Json.writes[User]
    implicit lazy val userJsonReader = Json.reads[User]
  }

}
