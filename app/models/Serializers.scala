package models

import play.api.libs.json._
import scala.reflect.ClassTag

sealed trait WritesId[T <: EntityId] extends Writes[T]
object WritesId {

  def apply[T <: EntityId] = new WritesId[T] {
    def writes(id: T): JsValue = JsString(id.value)
  }

  def apply[T <: EntityId](fromId: T => String) = new WritesId[T] {
    def writes(id: T): JsValue = JsString(fromId(id))
  }
}

sealed trait ReadsId[T <: EntityId] extends Reads[T]
object ReadsId {

  def apply[T <: EntityId : ClassTag] = new ReadsId[T] {

    override def reads(json: JsValue): JsResult[T] = json match {
      case JsString(value) =>
        val ClassT = implicitly[ClassTag[T]]
        value match {
          case ClassT(id) => JsSuccess(id)
          case other => JsError(s"Could not create new ${ClassT.getClass.getName}(" + other + ")")
        }
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