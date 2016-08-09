package serializers

import models.EntityId
import play.api.libs.json.Format

/**
 * Extend this with your companion class to provides serialization.
 *
 * @note this also fixes a bug in the scala compiler with case class companion objects not extending
 *       from (String => <companion class>) as it should by default.
 *
 * @tparam Id the type of [[EntityId]]
 */
trait IdSerializer[Id <: EntityId] extends (String => Id) {

  implicit val format: Format[Id] = StandardFormat id this
}
