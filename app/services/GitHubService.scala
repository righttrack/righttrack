package services

import scala.concurrent.{ExecutionContext, Future}
import play.api.Play
import play.api.libs.ws.WS
import play.api.libs.json.{JsString, JsObject, JsValue}


trait GitHubService {

  def getJson: Future[JsValue]

}

case class AccessToken(token: String) extends AnyVal

class WSGitHubService extends GitHubService {

  private[this] implicit val application = Play.current
  private[this] implicit val context = ExecutionContext.global

  def getJson = WS.url("https://api.github.com/orgs/octokit/repos").get() map { response =>
    JsObject(Seq(
      "response" -> JsObject(Seq(
        "source" -> JsString("GitHubServiceActor"),
        "data" -> response.json
      ))
    ))
  }

  def fetchPublicEvents(username: String)(implicit access_token: AccessToken): Future[JsValue] = {

    // todo: just gs et the events out of this
    WS.url(s"https://api.github.com/users/$username").get() map { response =>
      JsObject(Seq(
        "data" -> response.json
      ))
    }
  }
}

