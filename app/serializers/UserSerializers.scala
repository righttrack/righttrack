package serializers

import models.auth.AuthAccount
import models.users.User
import play.api.libs.json.Format

trait UserSerializers {
  self: Serializers =>

  implicit def authAccountFormat: Format[AuthAccount]

  implicit def userFormat: Format[User]
}
