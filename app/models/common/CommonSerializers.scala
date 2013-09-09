package models.common

import play.api.libs.json.Json

trait CommonSerializers {
  implicit val emailReads = Json.reads[Email]
  implicit val emailWrites = Json.writes[Email]
}
