package models.common

import models.{StringEntityIdSerializers, Serializers}
import play.api.libs.json._

/**
 * Provides common entity serializers for the web.
 */
trait CommonEntitySerializers extends StringEntityIdSerializers with CommonSerializers

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
