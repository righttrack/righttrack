package controllers.api

import play.api.libs.json.{JsValue, JsResult, Json}
import play.api.mvc.Controller
import serializers.ExternalReader

trait BaseController extends Controller {

  import scala.language.implicitConversions

  implicit def toExternalJson(json: Json.type): ExternalJson.type = ExternalJson
}

object ExternalJson {

  def fromExternalJson[T](reader: ExternalReader[T], json: JsValue): JsResult[T] =
    reader.external.reads(json)
}
