package serializers.api

import models._
import models.auth._
import models.meta.EntityTypes
import play.api.libs.json._
import serializers._

trait AuthIdSerializers extends EntityIdSerializers with StringEntityIdFormat {

  implicit val authAccountIdFormat: Format[AuthAccountId] = Format id AuthAccountId
}

object AuthSerializers extends Serializers
  with AuthIdSerializers
  with UserIdSerializers {

  private implicit def authAccountIdFormatImpl = authAccountIdFormat

  implicit lazy val oauthTokenTypeFormat = Format enum OAuthToken.TokenType
  implicit lazy val oauthTokenFormat = Json.format[OAuthToken]
  implicit lazy val oauthAccountFormat = Json.format[OAuthAccount]

  implicit lazy val authAccountFormat = Format[AuthAccount](
    Reads {
      json => (json \ "id").as[AnyEntityId](TypedEntityIdFormat.anyEntityIdFormat).entityType match {
        case EntityTypes.OAuthAccount => oauthAccountFormat.reads(json)
        case accountType => JsError(s"Unrecognized account type: $accountType")
      }
    },
    Writes {
      case it: OAuthAccount => oauthAccountFormat.writes(it)
    }
  )

}
