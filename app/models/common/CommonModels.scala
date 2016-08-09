package models.common

import play.api.libs.json.Format
import serializers.StandardFormat

case class Email(address: String)

object Email extends (String => Email) {
  implicit val format: Format[Email] = StandardFormat.asString(Email, _.address)
}
