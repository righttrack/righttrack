package serializers.api

import models._
import models.auth._
import models.meta.EntityTypes
import models.users.User
import models.users.UserId
import play.api.libs.json._
import scala.util.Failure
import scala.util.Success
import serializers._

trait UserIdSerializers extends EntityIdSerializers with StringEntityIdFormat {

  implicit lazy val userIdFormat: Format[UserId] = Format id UserId
}

object UserSerializers extends UserSerializers with Serializers with CommonSerializers
  with AuthIdSerializers
  with UserIdSerializers
{

  implicit lazy val oauthTokenTypeFormat: Format[OAuthToken.TokenType] = Format[OAuthToken.TokenType](
    Reads {
      json => OAuthToken.TokenType.tryFind(json.as[String]) match {
        case Success(value) => JsSuccess(value)
        case Failure(error) => JsError(error.getMessage)
      }
    },
    Writes {
      it => JsString(it.toString)
    }
  )
  implicit lazy val oauthTokenFormat: Format[OAuthToken] = Json.format[OAuthToken]
  implicit lazy val oauthAccountFormat: Format[OAuthAccount] = Json.format[OAuthAccount]

  override implicit lazy val authAccountFormat = Format[AuthAccount](
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
  override implicit lazy val userFormat: Format[User] = Json.format[User]
}
