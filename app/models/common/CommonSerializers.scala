package models.common

import models.{EntityId, Serializers}
import play.api.libs.json._

/**
 * Provides implicit writer for ALL EntityId subclasses.
 *
 * @note This is incompatible with [[models.TypedEntityIdSerializers]] and cannot be extended
 *       or imported into the same scope or else you will get an implicit ambiguity compiler error.
 */
trait StringEntityIdSerializers {

  /**
   * Writes the EntityId as a String.
   */
  implicit lazy val entityIdWriter: Writes[EntityId] =
    Writes[EntityId](id => JsString(id.value))
}

/**
 * Provides
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
