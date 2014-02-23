package models.activity.verb

import models.{Version, EntityId}
import scala.annotation.implicitNotFound

class Verb(val action: String)

@implicitNotFound(
  "No evidence found for ValidAction[${S}, ${V}, ${O}, ${VN}]. " +
    "Either import the type-class from ${V} " +
    "or add an implicit object that extends the ValidAction type arguments above to allow this action."
)
class ValidAction[S <: EntityId, V <: Verb, O <: EntityId, VN <: Version](implicit v: VN) {

  def version: VN = v
}