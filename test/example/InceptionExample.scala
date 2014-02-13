//import play.api.libs.json.{JsError, JsSuccess, JsResult}
//
////package example
////
////class InceptionExample {
////
////  val hey = 'yoson
////
////  hey
////}
//
//object UnapplyInt {
//
//  def unapply(result: JsResult[Any]): Option[Int] = result match {
//    case JsSuccess(x: Int, _) => Some(x)
//    case JsSuccess(_, _) => None
//    case JsError(errors) => None
//  }
//}
//
//object Example {
//
//  val x = JsSuccess(1)
//
//  x match {
//    case UnapplyInt(heyo) => println(heyo)
//  }
//}