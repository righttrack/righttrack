package serializers

import models.AnyEntityId
import models.meta.EntityType
import play.api.libs.functional.syntax._
import play.api.libs.json.{OFormat, __}

object MongoReads extends StandardReads with DefaultMongoFormats

object MongoWrites extends StandardWrites with DefaultMongoFormats

object MongoFormat extends StandardFormat with DefaultMongoFormats

trait DefaultMongoFormats extends CommonFormats {

  protected implicit def postfixOpsOk = scala.language.postfixOps

  implicit lazy val TypedEntityIdFormat: OFormat[AnyEntityId] = {
    val entityIdShape =
      (__ \ "_id").format(
        (__ \ "$oid").format[String] and
        (__ \ "entityType").format[EntityType] tupled
      )
    OFormat(
      id => entityIdShape.reads(id) map AnyEntityId.tupled,
      id => entityIdShape.writes(id.value, id.entityType)
    )
  }
}
