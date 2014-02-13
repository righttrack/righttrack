package models.common

import play.api.libs.json._
import models.{ReadsId, EntityId}
import play.api.libs.functional.syntax._

trait CommonSerializers {



  // todo flatten the serialized structure to a string for Email
  implicit val emailReader: Reads[Email] = new Reads[Email] {
    override def reads(json: JsValue): JsResult[Email] = Reads.email.reads(json) map Email
  }

  implicit val emailWriter: Writes[Email] = new Writes[Email] {
    override def writes(email: Email): JsValue = JsString(email.address)
  }

  implicit lazy val entityIdWriter: Writes[EntityId] = new Writes[EntityId] {
    def writes(id: EntityId): JsValue = JsString(id.value)
  }

//  implicit lazy val entityIdReader: Reads[EntityId] = ReadsId[EntityId]



}

object CommonSerializers extends CommonSerializers