package models.auth

import _root_.util.NamedValueEnum
import models.meta.{EntityType, EntityTypes}
import models.{AnyEntityId, Entity, EntityId}
import org.joda.time.DateTime
import play.api.libs.json._
import serializers.{DefaultSerializerFormats, IdSerializer}

sealed trait AuthAccount extends Entity {

  override val id: AuthAccountId
}

object AuthAccount extends DefaultSerializerFormats {

  implicit val format = Format[AuthAccount](
    Reads {
      json => (json \ "id").as[AnyEntityId].entityType match {
        case EntityTypes.OAuthAccount => Format.of[OAuthAccount].reads(json)
        case accountType => JsError(s"Unrecognized account type: $accountType")
      }
    },
    Writes {
      case it: OAuthAccount => Format.of[OAuthAccount].writes(it)
    }
  )
}

case class AuthAccountId(value: String) extends AnyVal with EntityId {
  override def entityType: EntityType = EntityTypes.OAuthAccount
}

object AuthAccountId extends IdSerializer[AuthAccountId]

case class OAuthAccount(id: AuthAccountId, token: OAuthToken)
  extends AuthAccount
  with Entity

object OAuthAccount extends DefaultSerializerFormats {

  implicit val format: Format[OAuthAccount] = Json.format[OAuthAccount]
}

case class OAuthToken(
  access: String,
  refresh: String,
  expires: DateTime,
  scopes: Set[String],
  kind: OAuthToken.TokenType
)

object OAuthToken {
  type TokenType = TokenType.Value

  implicit val format: Format[OAuthToken] = Json.format[OAuthToken]

  object TokenType extends NamedValueEnum {

    val Bearer = value("bearer")
  }
}