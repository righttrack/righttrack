package database.mongo

import _root_.util.typelevel.Tagging._
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{Json, JsObject, JsResult, JsValue}

object MongoJson {



  /**
   * Provided a Writes implicit for its type is available, convert any object into a JsValue.
   *
   * @param o Value to convert in Json.
   */
  def toJson[T](o: T)(implicit tjs: MongoJsonWrites[T]): JsValue @@ SafeForMongo = tjs.writeSafe(o)

  /**
   * Provided a Reads implicit for that type is available, convert a JsValue to any type.
   *
   * @param json Json value to transform as an instance of T.
   */
  def fromJson[T](json: JsValue)(implicit fjs: MongoJsonReads[T]): JsResult[T] = fjs.reads(json)

  /**
   * Creates an object that is explicitly SafeForMongo.
   */
  def obj(fields: (String, JsValueWrapper)*): JsObject @@ SafeForMongo = {
    tag[SafeForMongo](Json.obj(fields: _*))
  }
}
