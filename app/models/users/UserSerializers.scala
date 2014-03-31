package models.users

import models.auth._
import models.common.CommonSerializers
import models.meta.EntityTypes
import models.{StringEntityIdSerializers, TypedEntityIdSerializers, AnyEntityId}
import play.api.libs.json._
import scala.util.{Success, Failure}

object UserSerializers extends CommonSerializers {

  /*
   * Ids
   */
  implicit lazy val oauthAccountIdFormat: Format[OAuthAccountId] = {
    import TypedEntityIdSerializers._
    Format id OAuthAccountId
  }
  implicit lazy val userIdReads: Format[UserId] = {
    import StringEntityIdSerializers._
    Format id UserId
  }

  /*
   * Entities
   */

  implicit lazy val oauthTokenTypeFormat = Format[OAuthToken.TokenType](
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
  implicit lazy val oauthTokenFormat = Json.format[OAuthToken]
  implicit lazy val oauthAccountFormat = Json.format[OAuthAccount]
  implicit lazy val authAccountFormat = Format[AuthAccount](
    Reads {
      json => (json \ "id").as[AnyEntityId](TypedEntityIdSerializers.anyEntityIdFormat).entityType match {
        case EntityTypes.OAuthAccount => implicitly[Reads[OAuthAccount]].reads(json)
        case accountType => JsError(s"Unrecognized account type: $accountType")
      }
    },
    Writes {
      case it: OAuthAccount => implicitly[Writes[OAuthAccount]].writes(it)
    }
  )
  implicit lazy val userFormat = Json.format[User]
}
