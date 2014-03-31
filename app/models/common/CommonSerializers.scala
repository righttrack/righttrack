package models.common

import models.meta.EntityType
import models.{StringEntityIdSerializers, Serializers}
import play.api.libs.json._
import scala.util.Failure
import scala.util.Success

/**
 * Provides common entity serializers for the web.
 */
// TODO: Is this helpful or harmful?
trait CommonEntitySerializers extends StringEntityIdSerializers with CommonSerializers

trait CommonSerializers extends Serializers {

  implicit def emailFormat: Format[Email] = CommonSerializers.implEmailFormat

  implicit def entityTypeFormat: Format[EntityType] = CommonSerializers.implEntityTypeFormat
}

object CommonSerializers extends CommonSerializers {

  /**
   * A single implementation to prevent too many of these from being created.
   */
  private final val implEmailFormat: Format[Email] = Format(
    Reads {
      json => Reads.email.reads(json) map Email
    },
    Writes {
      email => JsString(email.address)
    }
  )

  private final val implEntityTypeFormat: Format[EntityType] = Format(
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
