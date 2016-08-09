package serializers

import models.common.Email
import models.meta.EntityType
import play.api.libs.json._

import scala.util.{Failure, Success}

trait CommonFormats {

  implicit lazy val emailFormat: Format[Email] = Format(
    Reads {
      json => Reads.email.reads(json) map Email
    },
    Writes {
      email => JsString(email.address)
    }
  )

  implicit lazy val entityTypeFormat: Format[EntityType] = Format(
    Reads {
      case JsString(idTypeName) => EntityType.tryFind(idTypeName) match {
        case Success(idType) => JsSuccess(idType)
        case Failure(error) => JsError(error.getMessage)
      }
      case js => JsError(s"Could not read EntityIdType from $js")
    },
    Writes {
      idType => JsString(idType.className)
    }
  )
}
