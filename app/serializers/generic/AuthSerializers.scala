package serializers.generic

import models.AnyEntityId
import models.auth._
import models.meta.EntityTypes
import play.api.libs.json._
import serializers._

trait AuthIdSerializers extends Serializers {
  self: SerializerFormat =>

  implicit val authAccountIdFormat: Format[AuthAccountId] = Format id AuthAccountId
}

trait AuthSerializers extends Serializers
with AuthIdSerializers
with UserIdSerializers {
  self: SerializerFormat =>

  implicit lazy val oauthTokenTypeFormat = Format enum OAuthToken.TokenType
  implicit lazy val oauthTokenFormat = Json.format[OAuthToken]
  implicit lazy val oauthAccountFormat = Json.format[OAuthAccount]

  implicit lazy val authAccountFormat = Format[AuthAccount](
    Reads {
      json => (json \ "id").as[AnyEntityId](self.typedEntityIdFormat).entityType match {
        case EntityTypes.OAuthAccount => oauthAccountFormat.reads(json)
        case accountType => JsError(s"Unrecognized account type: $accountType")
      }
    },
    Writes {
      case it: OAuthAccount => oauthAccountFormat.writes(it)
    }
  )

}
