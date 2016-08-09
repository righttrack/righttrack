package util.json

import play.api.data.validation.ValidationError
import play.api.libs.json._
import serializers.{StandardWrites, StandardReads, StandardFormat}

import scala.reflect.{ClassTag, classTag}
import scala.util.{Failure, Success, Try}

object PlayJson {

  object Format extends StandardFormat
  object Reads extends StandardReads
  object Writes extends StandardWrites

  @inline def toJson[T: Writes](value: T): JsValue = Json.toJson(value)

  @inline def fromJson[T: Reads](json: JsValue): JsResult[T] = Json.fromJson(json)

  def tryFromJson[T: Reads: ClassTag](json: JsValue): Try[T] = {
    Try(json.validate[T]) flatMap {
      case JsSuccess(value, _) => Success(value)
      case errors: JsError =>
        Failure(new PlayJsonFormatException(classTag[T].runtimeClass, json, errors))
    }
  }

  @inline def fromJsonOrThrow[T: Reads: ClassTag](json: JsValue): T = tryFromJson(json).get

  def errorMessage(cls: Class[_], json: JsValue, errors: Seq[(JsPath, Seq[ValidationError])]): String = {
    val jsonString = Json.prettyPrint(json)
    val errorsString = Json.prettyPrint(JsError.toFlatJson(errors))
    s"Could not parse an instance of ${cls.getName} from the following JSON:\n$jsonString\n\nErrors were:\n$errorsString\n"
  }

  @inline def errorMessage(cls: Class[_], json: JsValue, result: JsError): String =
    errorMessage(cls, json, result.errors)

}

class PlayJsonFormatException(cls: Class[_], json: JsValue, errors: JsError)
  extends RuntimeException(PlayJson.errorMessage(cls, json, errors))
