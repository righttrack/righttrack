package serializers

import models.common.Email
import models.meta.EntityType
import play.api.libs.json._
import scala.util.{Try, Failure, Success}
import org.joda.time.DateTime

/**
 * Provides common entity serializers for the web.
 */
trait CommonSerializers {
  self: Serializers =>

  implicit lazy val dateTimeFormat: Format[DateTime] = Format(
    Reads {
      case JsString(timestamp) => Try(DateTime.parse(timestamp)) match {
        case Success(x) => JsSuccess(x)
        case Failure(ex) => JsError(ex.getMessage)
      }
      case _ => JsError("Not a string")
    },
    Writes {
      timestamp => JsString(timestamp.toString)
    }
  )

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

object CommonSerializers extends Serializers with CommonSerializers