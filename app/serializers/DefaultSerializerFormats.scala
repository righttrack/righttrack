package serializers

import models.meta.EntityType
import models.{AnyEntityId, EntityId}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsString, OFormat, Writes, __}

/**
 * The default implicit serializers.
 */
trait DefaultSerializerFormats extends CommonFormats {

  protected implicit def postfixOpsOk = scala.language.postfixOps

  // TODO: Define better DateTime parsers
//  def defaultDateTimeReader: Reads[DateTime] = Reads.DefaultJodaDateReads
//  def defaultDateTimeWriter: Writes[DateTime] = Writes apply Writes.DefaultJodaDateWrites.writes

  val GenericEntityIdWriter: Writes[EntityId] = Writes(it => JsString(it.value))

  implicit lazy val TypedEntityIdFormat: OFormat[AnyEntityId] = {
    val anyEntityIdShape =
      (__ \ "id").format[String] and
      (__ \ "entityType").format[EntityType]
    anyEntityIdShape(AnyEntityId.apply, anyId => (anyId.value, anyId.entityType))
  }
}
