package models.common

import models.{EntityId, Serializers}
import play.api.libs.json._

trait CommonEntitySerializers extends CommonSerializers {

  implicit lazy val entityIdWriter: Writes[EntityId] =
    Writes[EntityId](id => JsString(id.value))
}

trait CommonSerializers extends Serializers {

  implicit def emailFormat: Format[Email] = CommonSerializers.implEmailFormat
}

object CommonSerializers extends CommonSerializers {

  /**
   * A single implementation to prevent too many of these from being created.
   */
  private final val implEmailFormat: Format[Email] = Format[Email](
    Reads(json => Reads.email.reads(json) map Email),
    Writes(email => JsString(email.address))
  )
}
