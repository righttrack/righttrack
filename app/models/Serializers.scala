package models

import play.api.libs.json._
import scala.reflect.ClassTag


sealed trait ReadsId[T <: EntityId] extends Reads[T]

object ReadsId {

  @deprecated("This doesn't work, so write your own Reads[T] {} ")
  def apply[T <: EntityId : ClassTag] = new ReadsId[T] {

    override def reads(json: JsValue): JsResult[T] = json match {
      case JsString(value) => JsSuccess(value.asInstanceOf[T])
      case _ => JsError("EntityId must be a String")
    }
  }

  def apply[T <: EntityId](toId: String => T) = new ReadsId[T] {
    override def reads(json: JsValue): JsResult[T] = json match {
      case JsString(value) => JsSuccess(toId(value))
      case _ => JsError("EntityId must be a String")
    }
  }
}