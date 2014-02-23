package services

import scala.concurrent.Future
import play.api.libs.json.{JsString, JsValue, __}
import play.api.libs.ws.WS
import scala.concurrent.ExecutionContext

class TrelloService {

  val ApiKey = "785525a19f4e6cb732bd4b7fb8810fb4"
  val ApiSecret = "52c792c6768bc96aceedd5e60b35d3881ec04bd0269982231d1eb0e14ad398b1"

  implicit val x = ExecutionContext.global

  val usernamePath = __ \ "username"

  def fetchMemberData: Future[String] = {
    WS.url(s"https://api.trello.com/1/members/farhansyed1?key=$ApiKey")
      .get()
      .map(i => usernamePath.asSingleJson(i.json) match {
      case JsString(username) => username
      case _ => ""
    })

  }
}