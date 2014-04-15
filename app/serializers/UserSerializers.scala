package serializers

import models.users.User
import play.api.libs.json.Format

trait UserSerializers {
  self: Serializers =>

  implicit def userFormat: Format[User]
}
