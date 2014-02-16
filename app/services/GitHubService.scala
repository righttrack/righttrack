package services

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.Actor
import play.api.Play
import play.api.libs.ws.WS
import play.api.libs.json.{JsString, JsObject, JsValue}
import scala.xml.NodeSeq

trait GitHubService {

  def getJson: Future[JsValue]

}

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

  def fetchPublicEvents(username: String): Future[JsValue] = {
    // todo: just get the events out of this
    WS.url(s"https://api.github.com/users/$username/events").get() map { response =>
      response.json
    }
  }
}

