package models.auth

import database.mongo.MongoTypedEntityIdSerializers
import models.{TypedEntityIdSerializers, AnyEntityId, Serializers}
import play.api.libs.json._
import models.meta.EntityTypes

object AuthSerializers extends Serializers with MongoTypedEntityIdSerializers {

  // TODO: Organize serializers better
  implicit private[this] def userIdReads = database.mongo.user.MongoUserSerializers.userIdReads

//  implicit class FormatOps(formats: Format.type) {
//
//    def enum[T <: Enumeration](enum: T): Format[enum.Value] = Format(
//      Reads {
//        json => enum.values.find(_ == json.as[String]) match {
//          case Some(value) => JsSuccess(value)
//          case None => JsError(s"Unrecognized value for $enum enumeration.")
//        }
//      },
//      Writes {
//        it => JsString(it.toString)
//      }
//    )
//  }

  implicit lazy val oauthTokenTypeFormat = Format enum OAuthToken.TokenType

  implicit lazy val oauthTokenFormat = Json.format[OAuthToken]

  implicit lazy val oauthAccountIdFormat = Format id OAuthAccountId

  implicit lazy val oauthAccountFormat = Json.format[OAuthAccount]

  implicit lazy val authAccountFormat = Format[AuthAccount](
    Reads {
      json => (json \ "id").as[AnyEntityId](TypedEntityIdSerializers.anyEntityIdFormat).entityType match {
        case EntityTypes.OAuthAccount => oauthAccountFormat.reads(json)
        case accountType => JsError(s"Unrecognized account type: $accountType")
      }
    },
    Writes {
      case it: OAuthAccount => implicitly[Writes[OAuthAccount]].writes(it)
    }
  )

}
